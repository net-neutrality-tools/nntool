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

    ///
    enum CodingKeys: String, CodingKey {
        case objectiveTimeoutNs = "udp_objective_timeout"

        case objectivePortOut = "udp_objective_out_port"
        case objectivePortIn  = "udp_objective_in_port"

        case objectivePacketCountOut = "udp_objective_out_num_packets"
        case objectivePacketCountIn  = "udp_objective_in_num_packets"
        case objectiveDelayNs        = "udp_objective_delay"

        case status = "udp_result_status" // not implemented on server

        case packetCountOut = "udp_result_out_num_packets"
        case packetCountIn  = "udp_result_in_num_packets"

        case responsePacketCountOut = "udp_result_out_response_num_packets"
        case responsePacketCountIn  = "udp_result_in_response_num_packets"

        case packetLossRateOut = "udp_result_out_packet_loss_rate"
        case packetLossRateIn  = "udp_result_in_packet_loss_rate"

        case rttsOutNs   = "udp_result_out_rtts_ns"
        case rttAvgOutNs = "udp_result_out_rtt_avg_ns"

        case rttsInNs   = "udp_result_in_rtts_ns"
        case rttAvgInNs = "udp_result_in_rtt_avg_ns"
    }
}
