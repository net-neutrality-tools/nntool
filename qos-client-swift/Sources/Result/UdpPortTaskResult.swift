/***************************************************************************
 * Copyright 2018-2019 alladin-IT GmbH
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
import ObjectMapper

public class UdpPortTaskResult: AbstractBidirectionalIpTaskResult {

    var objectivePacketCountOut: Int?
    var objectivePacketCountIn: Int?

    var objectiveDelayNs: UInt64?

    ////

    var packetCountOut: Int?
    var packetCountIn: Int?

    var responsePacketCountOut: Int?
    var responsePacketCountIn: Int?

    var packetLossRateOut: Double?
    var packetLossRateIn: Double?

    ////

    var rttsOutNs: [String: UInt64]?
    var rttAvgOutNs: UInt64?

    var rttsInNs: [String: UInt64]?
    var rttAvgInNs: UInt64?

    public override func mapping(map: Map) {
        super.mapping(map: map)

        objectiveTimeoutNs <- map["udp_objective_timeout"]

        objectivePortOut <- map["udp_objective_out_port"]
        objectivePortIn  <- map["udp_objective_in_port"]

        objectivePacketCountOut <- map["udp_objective_out_num_packets"]
        objectivePacketCountIn  <- map["udp_objective_in_num_packets"]
        objectiveDelayNs        <- map["udp_objective_delay"]

        status <- map["udp_result_status"] // not implemented on server

        packetCountOut <- map["udp_result_out_num_packets"]
        packetCountIn  <- map["udp_result_in_num_packets"]

        responsePacketCountOut <- map["udp_result_out_response_num_packets"]
        responsePacketCountIn  <- map["udp_result_in_response_num_packets"]

        packetLossRateOut <- map["udp_result_out_packet_loss_rate"]
        packetLossRateIn  <- map["udp_result_in_packet_loss_rate"]

        rttsOutNs   <- map["udp_result_out_rtts_ns"]
        rttAvgOutNs <- map["udp_result_out_rtt_avg_ns"]

        rttsInNs   <- map["udp_result_in_rtts_ns"]
        rttAvgInNs <- map["udp_result_in_rtt_avg_ns"]
    }
}
