// TODO: license
// inspired by https://github.com/rtr-nettest/open-rmbt-ios/blob/master/Sources/RMBTQoSUDPTest.m

import Foundation
import CocoaAsyncSocket
import nntool_shared_swift

///
struct VoipUdpStreamUtilConfiguration {

    var host: String
    var portOut: UInt16
    var portIn: UInt16?
    var writeOnly: Bool
    var timeoutNs: UInt64
    var delayNs: UInt64
    var packetCount: Int
    var uuid: String?
    var payload: String?

    var timeoutS: Double {
        return Double(timeoutNs / NSEC_PER_SEC)
    }
}

protocol VoipUdpStreamUtilDelegate: class {
    func udpStreamUtil(_ udpStreamUtil: VoipUdpStreamUtil, didBindToLocalPort port: UInt16)
    func udpStreamUtil(_ udpStreamUtil: VoipUdpStreamUtil, willSendPacketWithNumer packetNum: Int) -> (Data, Int)?
    func udpStreamUtil(_ udpStreamUtil: VoipUdpStreamUtil, didReceivePacket data: Data, atTimestamp timestamp: UInt64) -> Bool
}

///
class VoipUdpStreamUtil: NSObject {

    ///
    private let config: VoipUdpStreamUtilConfiguration

    ///
    weak var delegate: VoipUdpStreamUtilDelegate?

    ///
    private var status: QoSTaskStatus = .unknown

    private var stopReceivingSemaphore: DispatchSemaphore?
    private var delayElapsedSemaphore: DispatchSemaphore?

    private var sentPackets = 0
    private var receivedPackets = 0
    private var lastPacketSentAtNs: UInt64?

    ///
    init(config: VoipUdpStreamUtilConfiguration) {
        self.config = config
    }

    ///
    public func runStream() -> QoSTaskStatus {
        let delegateQueue = DispatchQueue(label: "at.alladin.nettest.qos.udpstream.delegate")
        let socket = GCDAsyncUdpSocket(delegate: self, delegateQueue: delegateQueue)

        do {
            if let portIn = config.portIn {
                logger.debug("socket.bind(\(portIn))")
                try socket.bind(toPort: portIn)
            }

            // Connect the socket even when binding because then we don't have to provide the host and port when sending
            try socket.connect(toHost: config.host, onPort: config.portOut)
            logger.debug("CONNECTED")
        } catch {
            logger.warning("could not connect/bind")
            logger.warning(error)

            socket.close()
            return .error
        }

        stopReceivingSemaphore = DispatchSemaphore(value: 0)
        let timeoutInterval = DispatchTimeInterval.nanoseconds(Int(config.timeoutNs))
        let timeoutDispatchTime = DispatchTime.now() + timeoutInterval

        if !config.writeOnly {
            do {
                logger.debug("beginReceiving")
                try socket.beginReceiving()
            } catch {
                logger.warning("beginReceiving error")
                logger.warning(error)

                socket.close()
                return .error
            }
        }

        var status: QoSTaskStatus = .unknown

        // send packets
        delayElapsedSemaphore = DispatchSemaphore(value: 0)

        logger.debug("Going to send \(config.packetCount) UDP packets")

        for i in 0..<config.packetCount {
            guard let (data, tag) = delegate?.udpStreamUtil(self, willSendPacketWithNumer: i) else {
                logger.debug("got no data to send -> continue")
                continue
            }

            lastPacketSentAtNs = TimeHelper.currentTimeNs()

            socket.send(data, withTimeout: config.timeoutS, tag: tag)

            guard let r = delayElapsedSemaphore?.wait(timeout: .now() + timeoutInterval), r != .timedOut else {
                logger.error("timeout waiting for send delay")
                status = .timeout
                break
            }

            logger.debug("after wait (send loop)")
        }

        if !config.writeOnly {
            logger.debug("BEFORE stopReceivingSemaphore?.wait")
            if let semaphoreResult = stopReceivingSemaphore?.wait(timeout: timeoutDispatchTime) {
                if semaphoreResult == .timedOut {
                    logger.warning("stopReceivingSemaphore timeout")

                    if status == .unknown {
                        status = .timeout
                    }
                }
            }
            logger.debug("AFTER stopReceivingSemaphore?.wait")
        }

        socket.closeAfterSending()

        if status == .unknown {
            status = .ok
        }

        return status
    }

    private func signalDelayElapsed() {
        _ = self.delayElapsedSemaphore?.signal()
    }
}

///
extension VoipUdpStreamUtil: GCDAsyncUdpSocketDelegate {

    func udpSocket(_ sock: GCDAsyncUdpSocket, didConnectToAddress address: Data) {
        logger.debug("didConnectToAddress \(address), \(sock.localPort())")
        delegate?.udpStreamUtil(self, didBindToLocalPort: sock.localPort())
    }

    ///
    func udpSocket(_ sock: GCDAsyncUdpSocket, didSendDataWithTag tag: Int) {
        logger.debug("didSendDataWithTag")

        sentPackets += 1

        var delay: UInt64 = 0

        if let l = lastPacketSentAtNs {
            let elapsed = TimeHelper.currentTimeNs() - l
            delay = elapsed > config.delayNs ? 0 : config.delayNs - elapsed
        }

        if delay > 0 {
            sock.delegateQueue()?.asyncAfter(deadline: .now() + .nanoseconds(Int(delay)), execute: signalDelayElapsed)
        } else {
            signalDelayElapsed()
        }
    }

    ///
    func udpSocket(_ sock: GCDAsyncUdpSocket, didReceive data: Data, fromAddress address: Data, withFilterContext filterContext: Any?) {

        let currentTimeNs = TimeHelper.currentTimeNs()
        receivedPackets += 1

        let shouldStopReceiving = delegate?.udpStreamUtil(self, didReceivePacket: data, atTimestamp: currentTimeNs) ?? false
        if shouldStopReceiving || receivedPackets == config.packetCount {
            _ = self.stopReceivingSemaphore?.signal()
        }
    }
}
