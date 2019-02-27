// TODO: license

import Foundation
import CocoaAsyncSocket

///
public class UdpPortTaskExecutor: AbstractBidirectionalIpTaskExecutor<UdpPortTaskConfiguration, UdpPortTaskResult>, GCDAsyncUdpSocketDelegate {
    
    private var receivedSequences = Set<UInt8>()
    private var packetsReceivedServer: Int?
    
    private var rttsNs: [String: UInt64]?
    
    override var taskType: TaskType? {
        return .udpPort
    }
    
    public override var result: UdpPortTaskResult {
        let r = super.result
      
        r.objectiveDelayNs = internalConfig.delayNs
        
        let packetCount = internalConfig.direction == .outgoing ? internalConfig.packetCountOut : internalConfig.packetCountIn
        var plr: Double?
        
        if let p = packetCount {
            let lostPackets = p - receivedSequences.count
            plr = round(min(max(0, Double(lostPackets) / Double(receivedSequences.count)), 1) * 100)
        }
        
        var rttAvgNs: UInt64?
        if let rtts = rttsNs, rtts.count > 0 {
            rttAvgNs = rtts.reduce(0) { $0 + $1.value } / UInt64(rtts.count)
        }
 
        switch internalConfig.direction {
        case .outgoing:
            r.objectivePacketCountOut = packetCount
            
            r.packetCountOut = packetsReceivedServer
            r.responsePacketCountOut = receivedSequences.count
            r.packetLossRateOut = plr
            
            r.rttsOutNs = rttsNs
            r.rttAvgOutNs = rttAvgNs
            
        case .incoming:
            r.objectivePacketCountIn = packetCount
            
            r.packetCountIn = receivedSequences.count
            r.responsePacketCountIn = packetsReceivedServer
            r.packetLossRateIn = plr
            
            r.rttsInNs = rttsNs
            r.rttAvgInNs = rttAvgNs
            
        default:
            break
        }
        
        return r
    }
    
    override public func main() {
        guard let host = internalConfig.serverAddress,
              let port = internalConfig.direction == .outgoing ? internalConfig.portOut : internalConfig.portIn,
              let packetCount = internalConfig.direction == .outgoing ? internalConfig.packetCountOut : internalConfig.packetCountIn,
              let delayNs = internalConfig.delayNs
            else {
                self.status = .error
                return
        }
        
        // TODO: check if all needed parameters are there and fail otherwise
        
        guard let qosTestUid = internalConfig.qosTestUid else {
            self.status = .error
            return
        }
        
        ///
        let udpStreamUtilConfig = UdpStreamUtilConfiguration(
            host: host,
            port: port,
            outgoing: internalConfig.direction == .outgoing,
            timeoutNs: internalConfig.timeoutNs,
            delayNs: delayNs,
            packetCount: packetCount,
            uuid: extractUuidFromToken(),
            payload: nil
        )
        
        let udpStreamUtil = UdpStreamUtil(config: udpStreamUtilConfig)
        ///
        
        // control connection request
        let cmd = String(format: "UDPTEST %@ %lu %lu +ID%d", internalConfig.direction.rawValue, port, packetCount, qosTestUid)
        
        do {
            let waitForAnswer = internalConfig.direction == .outgoing
            
            let response = try executeCommand(cmd: cmd, waitForAnswer: waitForAnswer)
            
            if waitForAnswer {
                guard let r = response, r.starts(with: "OK") else {
                    logger.warning("controlconnection response is not ok: ")
                    logger.warning(response)
                    self.status = .error
                    return
                }
            }
        } catch {
            logger.warning("control connection response error")
            logger.warning(error)
            self.status = .error
            return
        }
        
        ///
        let (streamUtilStatus, result) = udpStreamUtil.runStream()
        ///
        
        status = streamUtilStatus
        
        if let r = result {
            receivedSequences = r.receivedSequences
            rttsNs = r.rttsNs
        }
        
        ////////
        
        let resultCmd = String(format: "GET UDPRESULT %@ %lu +ID%d", internalConfig.direction.rawValue, port, qosTestUid)
        var resultCmdResponse: String?
        
        do {
            resultCmdResponse = try executeCommand(cmd: resultCmd, waitForAnswer: true)
            
            guard let r = resultCmdResponse, r.starts(with: "RCV") else {
                logger.warning("controlconnection response2 is not RCV: ")
                logger.warning(resultCmdResponse)
                status = .error
                return
            }
        } catch {
            logger.warning("control connection response2 error")
            logger.warning(error)
            status = .error
            return
        }

        if let components = resultCmdResponse?.components(separatedBy: " ") {
            if components.count < 2 {
                // command response could not be parsed
                status = .error
                return
            } else {
                packetsReceivedServer = Int(components[1])
                
                // incoming test rttsNs are provided by QoS service
                if internalConfig.direction == .incoming && components.count > 3 {
                    // we got rtts from qos-service as JSON
                    if let jsonData = components[3].data(using: .utf8) {
                        if let jsonDict = try? JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: UInt64] {
                            rttsNs = jsonDict
                        }
                    }
                }
            }
        }
        
        if status == .unknown {
            status = .ok
        }
    }
}
