/***************************************************************************
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
import CodableJSON
import nntool_shared_swift
import CocoaAsyncSocket

class SipTask: QoSControlConnectionTask {

    private enum Tag: Int {
        case invite = 1
        case trying = 2
        case ringing = 3
        case bye = 4
        case ok = 5
    }

    private var semaphore: DispatchSemaphore?

    private var port: UInt16
    private var count: Int
    private var callDurationNs: UInt64
    private var to: String
    private var from: String
    private var via: String?
    // payload -> body?

    //

    private var callSetupSuccessCount = 0
    private var callCompletionSuccessCount = 0

    private var lastTestStatus: QoSTaskStatus = .unknown

    ///
    enum CodingKeys4: String, CodingKey {
        case port
        case count
        case callDurationNs = "call_duration"
        case to
        case from
        case via
    }

    override var statusKey: String? {
        return "sip_result"
    }

    override var objectiveTimeoutKey: String? {
        return "sip_objective_timeout"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["sip_objective_port"] = JSON(port)
        r["sip_objective_count"] = JSON(count)
        r["sip_objective_call_curation"] = JSON(callDurationNs)
        r["sip_objective_to"] = JSON(to)
        r["sip_objective_from"] = JSON(from)
        r["sip_objective_via"] = JSON(via)

        //r["sip_result_to"] = JSON()
        //r["sip_result_from"] = JSON()
        //r["sip_result_via"] = JSON()
        //r["sip_result_duration"] = JSON()

        let ccsr = Float(callCompletionSuccessCount) / Float(count)
        let cssr = Float(callSetupSuccessCount) / Float(count)
        let dcr = 1.0 - ccsr

        r["sip_result_ccsr"] = JSON(ccsr)
        r["sip_result_cssr"] = JSON(ccsr)
        r["sip_result_dcr"] = JSON(dcr)

        taskLogger.debug("ccsr: \(ccsr), cssr: \(cssr), dcr: \(dcr)")

        return r
    }

    ///
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        port = try container.decodeWithStringFallback(UInt16.self, forKey: .port)
        count = try container.decodeWithStringFallback(Int.self, forKey: .count)
        callDurationNs = try container.decodeWithStringFallback(UInt64.self, forKey: .callDurationNs)

        to = try container.decode(String.self, forKey: .to)
        from = try container.decode(String.self, forKey: .from)
        via = try container.decodeIfPresent(String.self, forKey: .via)

        try super.init(from: decoder)
    }

    override func taskMain() {
        for _ in 0..<count {
            let testStatus = executeSipTest()
            if testStatus != .ok {
                status = testStatus
                break
            }
        }

        if status == .unknown {
            status = .ok
        }
    }

    private func executeSipTest() -> QoSTaskStatus {
        let cmd = String(format: "SIPTEST %lu +ID%d", port, uid)

        guard executeCommandAndAwaitOk(cmd: cmd) else {
            return .error
        }

        lastTestStatus = .unknown

        let delegateQueue = DispatchQueue(label: "eu.nntool.ios.qos.sip.delegate")
        let socket = GCDAsyncSocket(delegate: self, delegateQueue: delegateQueue)

        do {
            try socket.connect(toHost: controlConnectionParams.host, onPort: port)
        } catch {
            return .error
        }

        semaphore = DispatchSemaphore(value: 0)
        let semaphoreResult = semaphore?.wait(timeout: .now() + .nanoseconds(Int(timeoutNs)))

        socket.disconnect()

        if semaphoreResult == .timedOut {
            return .timeout
        } else {
            return lastTestStatus
        }
    }

    ///
    private func writeSipMessage(_ message: SipMessage, toSocket sock: GCDAsyncSocket, tag: Tag) {
        sock.write(message.data, withTimeout: TimeInterval(timeoutS), tag: tag.rawValue)
    }

    ///
    private func readSipResponse(tag: Tag, fromSocket sock: GCDAsyncSocket) {
        sock.readData(to: "\n\n".data(using: .utf8)!, withTimeout: TimeInterval(timeoutS), tag: tag.rawValue)
    }
}

extension SipTask: GCDAsyncSocketDelegate {

    func socket(_ sock: GCDAsyncSocket, didConnectToHost host: String, port: UInt16) {
        // send INVITE
        let inviteMessage = SipRequestMessage(type: .invite, from: from, to: to, via: via)
        //taskLogger.debug("send invite: \n\(String(data: inviteMessage.data, encoding: .utf8)!)")
        writeSipMessage(inviteMessage, toSocket: sock, tag: .invite)
    }

    func socketDidDisconnect(_ sock: GCDAsyncSocket, withError err: Error?) {
        if err != nil && lastTestStatus == .unknown {
            lastTestStatus = .error
        }

        semaphore?.signal()
    }

    func socket(_ sock: GCDAsyncSocket, didWriteDataWithTag tag: Int) {
        if let t = Tag(rawValue: tag) {
            switch t {
            case .invite:
                // read TRYING response
                readSipResponse(tag: .trying, fromSocket: sock)
            case .bye:
                // read OK response
                readSipResponse(tag: .ok, fromSocket: sock)
            default:
                break
            }
        }
    }

    func socket(_ sock: GCDAsyncSocket, didRead data: Data, withTag tag: Int) {
        if let t = Tag(rawValue: tag) {
            switch t {
            case .trying:
                // parse TRYING response, check type
                //taskLogger.debug("receive trying: \n\(String(data: data, encoding: .utf8)!)")
                guard let response = SipResponseMessage(data: data), response.type == .trying else {
                    lastTestStatus = .error
                    semaphore?.signal()
                    return
                }

                // read RINGING response
                readSipResponse(tag: .ringing, fromSocket: sock)
            case .ringing:
                // parse RINGING response, check type
                //taskLogger.debug("receive ringing: \n\(String(data: data, encoding: .utf8)!)")
                guard let response = SipResponseMessage(data: data), response.type == .ringing else {
                    lastTestStatus = .error
                    semaphore?.signal()
                    return
                }

                callSetupSuccessCount += 1

                usleep(useconds_t(callDurationNs / 1000))

                let byeMessage = SipRequestMessage(type: .bye, from: from, to: to, via: via)
                //taskLogger.debug("send bye: \n\(String(data: byeMessage.data, encoding: .utf8)!)")
                writeSipMessage(byeMessage, toSocket: sock, tag: .bye)
            case .ok:
                // parse OK response, check type
                //taskLogger.debug("receive ok: \n\(String(data: data, encoding: .utf8)!)")
                guard let response = SipResponseMessage(data: data), response.type == .ok else {
                    lastTestStatus = .error
                    semaphore?.signal()
                    return
                }

                callCompletionSuccessCount += 1

                lastTestStatus = .ok

                semaphore?.signal()
            default:
                break
            }
        }
    }
}
