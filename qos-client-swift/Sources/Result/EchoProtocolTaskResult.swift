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

public class EchoProtocolTaskResult: AbstractTaskResult {

    var objectiveHost: String?
    var objectivePort: UInt16?
    var objectiveProtocolType: EchoProtocolTaskConfiguration.ProtocolType?
    var objectivePayload: String?

    // TODO
    var result: String?

    // TODO:

    ///
    enum CodingKeys: String, CodingKey {
        case objectiveTimeoutNs = "echo_protocol_objective_timeout"

        case objectiveHost = "echo_protocol_objective_host"
        case objectivePort = "echo_protocol_objective_port"
        case objectiveProtocolType = "echo_protocol_objective_protocol"
        case objectivePayload = "echo_protocol_objective_payload"

        case status = "echo_protocol_status"
        case result = "echo_protocol_result"
    }
}
