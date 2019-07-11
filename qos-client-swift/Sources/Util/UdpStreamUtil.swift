// TODO: license
// inspired by https://github.com/rtr-nettest/open-rmbt-ios/blob/master/Sources/RMBTQoSUDPTest.m

import Foundation
import CocoaAsyncSocket
import nntool_shared_swift

///
struct UdpStreamUtilConfiguration {

    var host: String
    var port: UInt16
    var outgoing: Bool
    var timeoutNs: UInt64
    var delayNs: UInt64
    var packetCount: Int
    var uuid: String?
    var payload: String?

    var timeoutS: Double {
        return Double(timeoutNs / NSEC_PER_SEC)
    }
}

///
struct UdpStreamUtilResult {
    var receivedSequences = Set<UInt8>()
    var rttsNs: [String: UInt64]?
    var receivedPayload: String?
}

///
class UdpStreamUtil: NSObject {

    ///
    private enum Tag: Int {
        case outgoing
        case incomingResponse
    }

    ///
    public enum UdpPacketFlag: UInt8 {
        case oneDirection = 1
        case response = 2
        case awaitResponse = 3
    }

    ///
    private let config: UdpStreamUtilConfiguration

    private var status: QoSTaskStatus = .unknown

    private var stopReceivingSemaphore: DispatchSemaphore?
    private var delayElapsedSemaphore: DispatchSemaphore?

    private var lastPacketSentAtNs: UInt64?

    private var receivedSequences = Set<UInt8>()
    private var rttsNs: [String: UInt64]?

    private var receivedPayload: String?

    ///
    init(config: UdpStreamUtilConfiguration) {
        self.config = config
    }

    ///
    public func runStream() -> (QoSTaskStatus, UdpStreamUtilResult?) {
        let delegateQueue = DispatchQueue(label: "at.alladin.nettest.qos.udpstream.delegate")
        let socket = GCDAsyncUdpSocket(delegate: self, delegateQueue: delegateQueue)

        do {
            if config.outgoing {
                rttsNs = [String: UInt64]()
                try socket.connect(toHost: config.host, onPort: config.port)
            } else {
                try socket.bind(toPort: config.port)
            }
        } catch {
            logger.warning("could not connect/bind")
            logger.warning(error)
            return (.error, nil)
        }

        stopReceivingSemaphore = DispatchSemaphore(value: 0)
        let timeoutDispatchTime: DispatchTime = .now() + .nanoseconds(Int(config.timeoutNs))

        do {
            try socket.beginReceiving()
        } catch {
            logger.warning("beginReceiving error")
            logger.warning(error)
            socket.close() // TODO: necessary?
            return (.error, nil)
        }

        var status: QoSTaskStatus = .unknown

        // send packets if outgoing
        if config.outgoing {
            delayElapsedSemaphore = DispatchSemaphore(value: 0)

            for i in 0..<config.packetCount {
                lastPacketSentAtNs = TimeHelper.currentTimeNs()

                let data = dataForOutgoingPacket(flag: UdpPacketFlag.awaitResponse, sequenceNum: UInt8(i), outgoing: true)
                socket.send(data, withTimeout: config.timeoutS, tag: Tag.outgoing.rawValue)

                logger.debug("did send data (\(i))")

                if let semaphoreResult = delayElapsedSemaphore?.wait(timeout: .now() + .nanoseconds(Int(config.timeoutNs))) {
                    logger.debug("after wait")

                    if semaphoreResult == .timedOut {
                        logger.error("timeout waiting for send delay")
                        status = .timeout
                        break
                    }
                }
            }
        }

        if let semaphoreResult = stopReceivingSemaphore?.wait(timeout: timeoutDispatchTime) {
            if semaphoreResult == .timedOut {
                if status == .unknown {
                    status = .timeout
                }
                logger.warning("stopReceivingSemaphore timeout")
            }
        }

        socket.closeAfterSending()

        if status == .unknown {
            status = .ok
        }

        let result = UdpStreamUtilResult(
            receivedSequences: receivedSequences,
            rttsNs: rttsNs,
            receivedPayload: receivedPayload
        )

        return (status, result)
    }

    ///
    private func dataForOutgoingPacket(flag: UdpPacketFlag, sequenceNum: UInt8, outgoing: Bool) -> Data {
        var data = Data()

        if let customPayload = config.payload, let d = customPayload.data(using: .utf8) {
            data.append(d)
            return data
        }

        data.append(flag.rawValue)
        data.append(sequenceNum)

        if let uuidData = config.uuid?.data(using: .utf8) { /* .ascii ? */
            data.append(uuidData)
        }

        let currentTimeNs = TimeHelper.currentTimeNs()
        //logger.debug("currentTimeNs: \(currentTimeNs)")
        var currentTimeNsBigEndian = currentTimeNs.bigEndian
        data.append(UnsafeBufferPointer(start: &currentTimeNsBigEndian, count: 1))

        return data
    }

    ///
    private func packetDataByReplacingFlag(_ flag: UdpPacketFlag, data: Data) -> Data {
        var d = Data()

        d.append(flag.rawValue)
        d.append(data[data.startIndex.advanced(by: 1)..<data.endIndex])

        return d
    }
}

///
extension UdpStreamUtil: GCDAsyncUdpSocketDelegate {

    ///
    func udpSocket(_ sock: GCDAsyncUdpSocket, didSendDataWithTag tag: Int) {
        if let tagEnum = Tag(rawValue: tag), tagEnum == .outgoing {
            var delay: UInt64 = 0

            if let l = lastPacketSentAtNs {
                let elapsed = TimeHelper.currentTimeNs() - l
                //logger.debug("elapsed: \(elapsed)")
                delay = elapsed > config.delayNs ? 0 : config.delayNs - elapsed
            }

            let signalBlock = {
                //logger.debug("signal delayElaspsedSemaphore")
                _ = self.delayElapsedSemaphore?.signal()
            }

            if delay > 0 {
                sock.delegateQueue()?.asyncAfter(deadline: .now() + .nanoseconds(Int(delay)), execute: signalBlock)
            } else {
                signalBlock()
            }
        }
    }

    ///
    func udpSocket(_ sock: GCDAsyncUdpSocket, didReceive data: Data, fromAddress address: Data, withFilterContext filterContext: Any?) {
        let currentTimeNs = TimeHelper.currentTimeNs()

        let signalBlock = {
            _ = self.stopReceivingSemaphore?.signal()
        }

        if let _ = config.payload {
            if config.outgoing {
                receivedPayload = String(data: data, encoding: .utf8)
                signalBlock()
            }

            return
        }

        if data.endIndex < 1+1+36+8 { // packet data out of bounds
            status = .error
            signalBlock()
            return
        }

        var flag: UInt8 = 0
        data.copyBytes(to: &flag, count: 1)

        guard let flagEnum = UdpPacketFlag(rawValue: flag) else {
            // unknown packet flag -> return
            logger.debug("received unknown packet flag \(flag)")
            return
        }
        logger.debug("received flag: \(flagEnum)")

        var index = 1

        var sequenceNum: UInt8 = 0
        data.copyBytes(to: &sequenceNum, from: data.startIndex.advanced(by: index)..<data.startIndex.advanced(by: index + 2))

        logger.debug("received sequenceNum: \(sequenceNum)")

        index += 1

        //let uuidData = data[data.startIndex.advanced(by: index)..<data.startIndex.advanced(by: index + 36)]
        //let uuid = String(data: uuidData, encoding: .utf8)

        //logger.debug("uuid: \(uuid)")

        if config.outgoing {
            index += 36

            let sentTimeNsData = data[data.startIndex.advanced(by: index)..<data.startIndex.advanced(by: index + 8)]
            let sentTimeNs: UInt64 = sentTimeNsData.withUnsafeBytes { UInt64(bigEndian: $0.pointee) } // TODO: if etc.

            rttsNs?["\(sequenceNum)"] = currentTimeNs - sentTimeNs
        }

        if receivedSequences.contains(sequenceNum) {
            logger.warning("received duplicate")
            status = .error
            signalBlock()
        } else {
            receivedSequences.insert(sequenceNum)

            if config.outgoing {
                assert(flagEnum == UdpPacketFlag.response)

                if receivedSequences.count == config.packetCount {
                    signalBlock()
                }
            } else {
                assert(flagEnum == UdpPacketFlag.awaitResponse)

                sock.send(packetDataByReplacingFlag(.response, data: data), toAddress: address, withTimeout: config.timeoutS, tag: Tag.incomingResponse.rawValue)

                if receivedSequences.count == config.packetCount {
                    // add delay to allow the last confirmation packet to reach the server
                    sock.delegateQueue()?.asyncAfter(deadline: .now() + .nanoseconds(Int(config.delayNs)), execute: signalBlock)
                }
            }
        }
    }
}
