import Foundation
import CodableJSON
import nntool_shared_swift

struct RTPControlData {
    var rtpPacket: RTPPacket
    var receivedNs: UInt64
}

class VoipTask: QoSControlConnectionTask {

    private var bitsPerSample: UInt8 = 8
    private var sampleRate: UInt16 = 8000 // 8 kHz
    private var callDurationNs: UInt64 = 1 * NSEC_PER_SEC // 1s
    private var portOut: UInt16?
    private var portIn: UInt16?
    private var delayNs: UInt64 = 20 * NSEC_PER_MSEC // 20ms
    private var payloadType: UInt8 = 8 // PCMA(8, 8000, 1, CodecType.AUDIO)
    private var bufferNs: UInt64 = 100 * NSEC_PER_MSEC // 100ms buffer

    private var initialSequenceNumber = UInt16(arc4random_uniform(10000) + 1)

    private var payloadSize: Int!
    private var rtpTimestamp: UInt32!
    private var initialRtpPacket: RTPPacket!
    private var voipStremStartTimeNs: UInt64!

    private var receivedRtpPackets = [UInt16: RTPControlData]()
    private var resultPortIn: UInt16?

    private var remoteResultMaxJitter: Int?
    private var remoteResultMeanJitter: Int?
    private var remoteResultMaxDelta: Int?
    private var remoteResultSkew: Int?
    private var remoteResultNumPackets: Int?
    private var remoteResultSequenceErrors: Int?
    private var remoteResultShortSequential: Int?
    private var remoteResultLongSequential: Int?
    private var remoteResultStalls: Int?
    private var remoteResultAvgStallTime: Int?

    private var localResultMaxJitter: Int64?
    private var localResultMeanJitter: Int64?
    private var localResultMaxDelta: Int64?
    private var localResultSkew: Int64?
    private var localResultNumPackets: Int?
    private var localResultSequenceErrors: Int?
    private var localResultShortSequential: Int?
    private var localResultLongSequential: Int?
    private var localResultStalls: Int?
    private var localResultAvgStallTime: Int64?

    ///
    enum CodingKeys4: String, CodingKey {
        case bitsPerSample = "bits_per_sample"
        case sampleRate = "sample_rate"
        case callDurationNs = "call_duration"
        case portOut = "out_port"
        case portIn = "in_port"
        case delayNs = "delay"
        case payloadType = "payload"
        case bufferNs = "buffer"
    }

    override var statusKey: String? {
        return "voip_result_status"
    }

    override var objectiveTimeoutKey: String? {
        return "voip_objective_timeout"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["voip_objective_bits_per_sample"] = JSON(bitsPerSample)
        r["voip_objective_sample_rate"] = JSON(sampleRate)
        r["voip_objective_call_duration"] = JSON(callDurationNs)
        r["voip_objective_out_port"] = JSON(portOut)
        r["voip_objective_in_port"] = JSON(portIn ?? resultPortIn) // TODO: this should be a result and not an objective
        r["voip_objective_delay"] = JSON(delayNs)
        r["voip_objective_payload"] = JSON(payloadType)
        r["voip_objective_buffer"] = JSON(bufferNs)

        r["voip_result_out_max_jitter"] = JSON(remoteResultMaxJitter)
        r["voip_result_out_mean_jitter"] = JSON(remoteResultMeanJitter)
        r["voip_result_out_max_delta"] = JSON(remoteResultMaxDelta)
        r["voip_result_out_skew"] = JSON(remoteResultSkew)
        r["voip_result_out_num_packets"] = JSON(remoteResultNumPackets ?? 0)
        r["voip_result_out_sequence_error"] = JSON(remoteResultSequenceErrors)
        r["voip_result_out_short_seq"] = JSON(remoteResultShortSequential)
        r["voip_result_out_long_seq"] = JSON(remoteResultLongSequential)
        r["voip_result_out_stalls"] = JSON(remoteResultStalls)
        r["voip_result_out_avg_stall_time"] = JSON(remoteResultAvgStallTime)

        r["voip_result_in_max_jitter"] = JSON(localResultMaxJitter)
        r["voip_result_in_mean_jitter"] = JSON(localResultMeanJitter)
        r["voip_result_in_max_delta"] = JSON(localResultMaxDelta)
        r["voip_result_in_skew"] = JSON(localResultSkew)
        r["voip_result_in_num_packets"] = JSON(localResultNumPackets ?? 0)
        r["voip_result_in_sequence_error"] = JSON(localResultSequenceErrors)
        r["voip_result_in_short_seq"] = JSON(localResultShortSequential)
        r["voip_result_in_long_seq"] = JSON(localResultLongSequential)
        r["voip_result_in_stalls"] = JSON(localResultStalls)
        r["voip_result_in_avg_stall_time"] = JSON(localResultAvgStallTime)

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        if let serverBitsPerSample = try container.decodeIfPresent(UInt8.self, forKey: .bitsPerSample) {
            bitsPerSample = serverBitsPerSample
        }

        if let serverSampleRate = try container.decodeIfPresent(UInt16.self, forKey: .sampleRate) {
            sampleRate = serverSampleRate
        }

        var serverCallDurationNs = try? container.decodeIfPresent(UInt64.self, forKey: .callDurationNs)
        if serverCallDurationNs == nil {
            if let serverCallDurationNsString = try? container.decodeIfPresent(String.self, forKey: .callDurationNs) {
                serverCallDurationNs = UInt64(serverCallDurationNsString)
            }
        }
        
        if let cdns = serverCallDurationNs {
            callDurationNs = cdns
        }

        portOut = try? container.decodeIfPresent(UInt16.self, forKey: .portOut)
        if portOut == nil {
            if let portOutString = try? container.decodeIfPresent(String.self, forKey: .portOut) {
                portOut = UInt16(portOutString)
            }
        }

        portIn = try? container.decodeIfPresent(UInt16.self, forKey: .portIn)
        if portIn == nil {
            if let portInString = try? container.decodeIfPresent(String.self, forKey: .portIn) {
                portIn = UInt16(portInString)
            }
        }

        if let serverDelayNs = try container.decodeIfPresent(UInt64.self, forKey: .delayNs) {
            delayNs = serverDelayNs
        }

        if let serverPayloadType = try container.decodeIfPresent(UInt8.self, forKey: .payloadType) {
            payloadType = serverPayloadType
        }

        if let serverBufferNs = try container.decodeIfPresent(UInt64.self, forKey: .bufferNs) {
            bufferNs = serverBufferNs
        }

        try super.init(from: decoder)
    }

    override func taskMain() {
        guard let portOut = portOut else {
            status = .error
            return
        }

        let cmd = String(format: "VOIPTEST %d %d %d %d %lu %lu %d %d %lu",
                         portOut, portIn ?? 0, sampleRate, bitsPerSample,
                         delayNs / NSEC_PER_MSEC, callDurationNs / NSEC_PER_MSEC, initialSequenceNumber,
                         payloadType, bufferNs)

        var ssrc: UInt32

        do {
            let response = try executeCommand(cmd: cmd, waitForAnswer: true)

            guard let r = response, r.starts(with: "OK") else {
                status = .error
                return
            }

            let parts = r.trimmingCharacters(in: .whitespacesAndNewlines).components(separatedBy: " ")

            guard parts.count > 1, let ssrc2 = UInt32(parts[1]), ssrc2 > 0 else {
                status = .error
                return
            }

            ssrc = ssrc2
        } catch {
            status = .error
            return
        }

        voipStremStartTimeNs = TimeHelper.currentTimeNs()

        let delayMs          = Double(delayNs / NSEC_PER_MSEC)
        let sampleRateDouble = Double(sampleRate)

        payloadSize  = Int(sampleRateDouble / (1000 / delayMs) * (Double(bitsPerSample) / 8))
        rtpTimestamp = UInt32(sampleRateDouble / (1000 / delayMs))

        //taskLogger.debug("payloadSize: \(payloadSize), rtpTimestamp: \(rtpTimestamp)")

        initialRtpPacket = RTPPacket(
            header: RTPHeader(
                flags: RTPHeaderFlags(
                    version: 2,
                    hasPadding: false,
                    hasExtension: false,
                    csrcCount: 0,
                    hasMarker: true, // first packet has marker set to true
                    payloadType: payloadType
                ),
                sequenceNumber: initialSequenceNumber,
                timestamp: 0,
                ssrcIdentifier: ssrc
            ), payload: Data()
        )

        let udpStreamUtilConfig = UdpStreamUtilConfiguration(
            host: controlConnectionParams.host,
            portOut: portOut,
            portIn: portIn,
            respondOnly: false,
            timeoutNs: timeoutNs,
            delayNs: delayNs,
            packetCount: Int(callDurationNs / delayNs)
        )

        let udpStreamUtil = UdpStreamUtil(config: udpStreamUtilConfig)
        udpStreamUtil.delegate = self

        let streamUtilStatus = udpStreamUtil.runStream()
        guard streamUtilStatus == .ok else {
            status = streamUtilStatus
            return
        }

        usleep(100 * 1000) // Wait a short amount of time (100ms) until requesting results

        let remoteResultStatus = requestRemoteResults(ssrc)
        guard remoteResultStatus == .ok else {
            status = remoteResultStatus
            return
        }

        calculateIncomingResults()

        if status == .unknown {
            status = .ok
        }
    }

    private func requestRemoteResults(_ ssrc: UInt32) -> QoSTaskStatus {
        let resultCmd = "GET VOIPRESULT \(ssrc)"
        do {
            let response = try executeCommand(cmd: resultCmd, waitForAnswer: true)

            guard let r = response, r.starts(with: "VOIPRESULT") else {
                return .error

            }

            let voipResultParts = r.trimmingCharacters(in: .whitespacesAndNewlines).components(separatedBy: " ").map { Int($0) }

            guard voipResultParts.count > 10 else {
                return .error
            }

            remoteResultMaxJitter       = voipResultParts[1]
            remoteResultMeanJitter      = voipResultParts[2]
            remoteResultMaxDelta        = voipResultParts[3]
            remoteResultSkew            = voipResultParts[4]
            remoteResultNumPackets      = voipResultParts[5]
            remoteResultSequenceErrors  = voipResultParts[6]
            remoteResultShortSequential = voipResultParts[7]
            remoteResultLongSequential  = voipResultParts[8]

            remoteResultStalls          = voipResultParts[9]
            remoteResultAvgStallTime    = voipResultParts[10]
        } catch {
            return .error
        }

        return .ok
    }

    private func calculateIncomingResults() {
        guard receivedRtpPackets.count > 0 else {
            return
        }

        var sequenceNumberJitterDict = [UInt16: Double]()

        let sequenceNumbers = [UInt16](receivedRtpPackets.keys).sorted()

        var skew: Int64 = 0
        var maxDelta: Int64 = 0
        var maxJitter: Int64 = 0

        var stalls = 0
        var stallTimeNs: Int64 = 0

        var previousSequenceNumber: UInt16?
        for seqNum in sequenceNumbers {
            guard let cur = receivedRtpPackets[seqNum] else { continue }

            if let prevSeqNum = previousSequenceNumber {
                guard let prev = receivedRtpPackets[prevSeqNum] else { break }

                let tsDiff = Int64(cur.receivedNs) - Int64(prev.receivedNs) // Can be negative if previous packet arrives delayed
                if bufferNs < tsDiff {
                    stalls += 1
                    stallTimeNs += tsDiff - Int64(bufferNs)
                }

                let rtpTsDiff = Double(cur.rtpPacket.header.timestamp - prev.rtpPacket.header.timestamp)
                let rtpTsDiffDivSampleRate = (rtpTsDiff / Double(sampleRate) * 1000) * Double(NSEC_PER_MSEC)

                skew += Int64(rtpTsDiffDivSampleRate) - tsDiff

                let delta = abs(tsDiff - Int64(rtpTsDiffDivSampleRate))

                guard let prevJitter = sequenceNumberJitterDict[prevSeqNum] else { break }
                let jitter = prevJitter + (Double(delta) - prevJitter) / 16

                sequenceNumberJitterDict[seqNum] = jitter

                maxDelta = max(maxDelta, delta)
                maxJitter = max(maxJitter, Int64(jitter))
            } else {
                sequenceNumberJitterDict[seqNum] = 0
            }

            previousSequenceNumber = seqNum
        }

        let meanJitter = Int64(sequenceNumberJitterDict.compactMap { $0.value }.reduce(0, +) / Double(sequenceNumberJitterDict.count))

        //

        let sequenceNumbersSortedByTimestamp =
            [RTPControlData](receivedRtpPackets.values)
                .sorted(by: { $0.receivedNs < $1.receivedNs })
                .map { $0.rtpPacket.header.sequenceNumber }

        var packetsOutOfOrder = 0

        var minSequential = 0
        var maxSequential = 0
        var curSequential = 0

        var nextSeq = initialSequenceNumber
        for seqNum in sequenceNumbersSortedByTimestamp {
            if seqNum == nextSeq {
                curSequential += 1
            } else {
                packetsOutOfOrder += 1

                maxSequential = max(maxSequential, curSequential)

                if curSequential > 1 {
                    if curSequential < minSequential {
                        minSequential = curSequential
                    } else {
                        minSequential = minSequential == 0 ? curSequential : minSequential
                    }
                }

                curSequential = 0
            }

            nextSeq += 1
        }

        maxSequential = max(maxSequential, curSequential)

        if curSequential > 1 {
            if curSequential < minSequential {
                minSequential = curSequential
            } else {
                minSequential = minSequential == 0 ? curSequential : minSequential
            }
        }

        if minSequential == 0 && maxSequential > 0 {
            minSequential = maxSequential
        }

        //

        localResultMaxJitter = maxJitter
        localResultMeanJitter = meanJitter
        localResultMaxDelta = maxDelta
        localResultSkew = skew
        localResultNumPackets = sequenceNumberJitterDict.count
        localResultSequenceErrors = packetsOutOfOrder
        localResultShortSequential = minSequential
        localResultLongSequential = maxSequential

        localResultStalls       = stalls
        localResultAvgStallTime = stalls == 0 ? 0 : stallTimeNs / Int64(stalls)
    }
}

extension VoipTask: UdpStreamUtilDelegate {

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, didBindToLocalPort port: UInt16) {
        taskLogger.debug("ON BIND (local port: \(port))")
        resultPortIn = port
    }

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, willSendPacketWithNumer packetNum: Int) -> (Data, UdpStreamUtil.Tag)? {
        taskLogger.debug("ON SEND (packet: \(packetNum))")

        if packetNum > 0 {
            initialRtpPacket.header.sequenceNumber += 1
            initialRtpPacket.header.timestamp += rtpTimestamp
            initialRtpPacket.header.flags.hasMarker = false
        }

        var payloadData = Data(count: payloadSize)

        guard payloadData.withUnsafeMutableBytes({
            // It should be safe to unwrap baseAddress
            // (see https://stackoverflow.com/questions/39820602/using-secrandomcopybytes-in-swift ).
            SecRandomCopyBytes(kSecRandomDefault, payloadSize, $0.baseAddress!)
        }) == errSecSuccess else {
            status = .error
            return nil
        }

        initialRtpPacket.payload = payloadData

        return (initialRtpPacket.rawValue, .outgoing)
    }

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, didReceiveData data: Data, fromAddress address: Data, atTimestamp timestamp: UInt64) -> Bool {
        taskLogger.debug("ON RECEIVE \(timestamp) -> \(data.count) bytes")

        guard let receivedPacket = RTPPacket(rawValue: data) else {
            status = .error // TODO: does this error get to VoipTask? udpstreamutil will still report .ok -> check first if status != unknown before checking udpstreamutil status
            return true // Could not parse received packet -> abort task
        }
        taskLogger.debug(receivedPacket)

        receivedRtpPackets[receivedPacket.header.sequenceNumber] = RTPControlData(rtpPacket: receivedPacket, receivedNs: timestamp)

        return false
    }
}
