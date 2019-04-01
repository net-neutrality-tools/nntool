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

///
public class TcpPortTaskResult: AbstractBidirectionalIpTaskResult {

    var resultOut: Status?
    var resultIn: Status?

    var resultOutResponse: String?
    var resultInResponse: String?

    var resultErrorDetails: String?

    ///
    enum CodingKeys: String, CodingKey {
        case objectiveTimeoutNs = "tcp_objective_timeout" // TODO: use generic "objective_timeout"...

        case objectivePortOut   = "tcp_objective_out_port"
        case objectivePortIn    = "tcp_objective_in_port"

        case resultOut          = "tcp_result_out"
        case resultIn           = "tcp_result_in"

        case resultOutResponse  = "tcp_result_out_response"
        case resultInResponse   = "tcp_result_in_response"

        case resultErrorDetails = "tcp_result_error_details"
    }
}
