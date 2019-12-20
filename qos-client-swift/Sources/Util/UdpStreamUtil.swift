/***************************************************************************
 * Copyright 2017 appscape gmbh
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

import Foundation
import CocoaAsyncSocket
import nntool_shared_swift

///
struct UdpStreamUtilConfiguration {

    var host: String?
    var portOut: UInt16?
    var portIn: UInt16?
    var respondOnly: Bool // respondOny == true if portOut and host are not set?
    var timeoutNs: UInt64
    var delayNs: UInt64
    var packetCount: Int
    var payload: String?

    var timeoutS: Double {
        return Double(timeoutNs / NSEC_PER_SEC)
    }
}

protocol UdpStreamUtilDelegate: class {
    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, didBindToLocalPort port: UInt16)
    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, willSendPacketWithNumer packetNum: Int) -> (Data, UdpStreamUtil.Tag)?
    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, didReceiveData data: Data, fromAddress address: Data, atTimestamp timestamp: UInt64) -> Bool
}

///
class UdpStreamUtil: NSObject {

    enum Tag: Int {
        case outgoing
        case noDelay
        case incomingResponse
    }

    ///
    private let config: UdpStreamUtilConfiguration

    ///
    weak var delegate: UdpStreamUtilDelegate?

    ///
    private var status: QoSTaskStatus = .unknown

    private var socket: GCDAsyncUdpSocket?

    private var stopReceivingSemaphore: DispatchSemaphore?
    private var delayElapsedSemaphore: DispatchSemaphore?

    private var sentPackets = 0
    private var receivedPackets = 0
    private var lastPacketSentAtNs: UInt64?

    ///
    init(config: UdpStreamUtilConfiguration) {
        self.config = config
    }

    ///
    public func runStream() -> QoSTaskStatus {
        let delegateQueue = DispatchQueue(label: "at.alladin.nettest.qos.udpstream.delegate")
        socket = GCDAsyncUdpSocket(delegate: self, delegateQueue: delegateQueue)

        do {
            if let portIn = config.portIn {
                logger.debug("socket.bind(\(portIn))")
                try socket?.bind(toPort: portIn)
            }

            if let host = config.host, let portOut = config.portOut, !config.respondOnly {
                // Connect the socket even when binding because then we don't have to provide the host and port when sending
                try socket?.connect(toHost: host, onPort: portOut)
                logger.debug("CONNECTED")
            }
        } catch {
            logger.warning("could not connect/bind")
            logger.warning(error)

            socket?.close()
            return .error
        }

        stopReceivingSemaphore = DispatchSemaphore(value: 0)
        let timeoutInterval = DispatchTimeInterval.nanoseconds(Int(config.timeoutNs))
        let timeoutDispatchTime = DispatchTime.now() + timeoutInterval

        do {
            logger.debug("beginReceiving")
            try socket?.beginReceiving()
        } catch {
            logger.warning("beginReceiving error")
            logger.warning(error)

            socket?.close()
            return .error
        }

        var status: QoSTaskStatus = .unknown

        // send packets if !responseOnly
        if !config.respondOnly {
            delayElapsedSemaphore = DispatchSemaphore(value: 0)

            logger.debug("Going to send \(config.packetCount) UDP packets")

            for i in 0..<config.packetCount {
                guard let (data, tag) = delegate?.udpStreamUtil(self, willSendPacketWithNumer: i) else {
                    logger.debug("got no data to send -> continue")
                    continue
                }

                lastPacketSentAtNs = TimeHelper.currentTimeNs()

                socket?.send(data, withTimeout: config.timeoutS, tag: tag.rawValue)

                guard let r = delayElapsedSemaphore?.wait(timeout: .now() + timeoutInterval), r != .timedOut else {
                    logger.error("timeout waiting for send delay")
                    status = .timeout
                    break
                }

                logger.debug("after wait (send loop)")
            }
        }

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

        socket?.closeAfterSending()

        if status == .unknown {
            status = .ok
        }

        return status
    }

    private func signalDelayElapsed() {
        _ = self.delayElapsedSemaphore?.signal()
    }

    func sendResponse(_ data: Data, toAddress address: Data, tag: Tag) {
        socket?.send(data, toAddress: address, withTimeout: config.timeoutS, tag: tag.rawValue)
    }
}

///
extension UdpStreamUtil: GCDAsyncUdpSocketDelegate {

    func udpSocket(_ sock: GCDAsyncUdpSocket, didConnectToAddress address: Data) {
        logger.debug("didConnectToAddress \(address), \(sock.localPort())")
        delegate?.udpStreamUtil(self, didBindToLocalPort: sock.localPort())
    }

    ///
    func udpSocket(_ sock: GCDAsyncUdpSocket, didSendDataWithTag tag: Int) {
        logger.debug("didSendDataWithTag")

        sentPackets += 1

        guard let tagEnum = Tag(rawValue: tag) else {
            return
        }

        switch tagEnum {
        case .noDelay:
            signalDelayElapsed()
        case .outgoing:
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
        default:
            break
        }
    }

    ///
    func udpSocket(_ sock: GCDAsyncUdpSocket, didReceive data: Data, fromAddress address: Data, withFilterContext filterContext: Any?) {

        let currentTimeNs = TimeHelper.currentTimeNs()
        receivedPackets += 1

        let shouldStopReceiving = delegate?.udpStreamUtil(self, didReceiveData: data, fromAddress: address, atTimestamp: currentTimeNs) ?? false
        if shouldStopReceiving || receivedPackets == config.packetCount {
            _ = self.stopReceivingSemaphore?.signal()
        }
    }
}
