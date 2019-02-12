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

public class EchoProtocolTaskResult: AbstractTaskResult {
 
    var objectiveHost: String?
    var objectivePort: UInt16?
    var objectiveProtocolType: EchoProtocolTaskConfiguration.ProtocolType?
    var objectivePayload: String?

    // TODO
    var result: String?
    
    // TODO:
    
    public override func mapping(map: Map) {
        super.mapping(map: map)
        
        objectiveTimeoutNs <- map["echo_protocol_objective_timeout"]
        
        objectiveHost <- map["echo_protocol_objective_host"]
        objectivePort <- map["echo_protocol_objective_port"]
        objectiveProtocolType <- (map["echo_protocol_objective_protocol"], EchoProtocolTaskConfiguration.ProtocolTypeTransformOf)
        objectivePayload <- map["echo_protocol_objective_payload"]
        
        status <- (map["echo_protocol_status"], EnumTransform<Status>())
        result <- map["echo_protocol_result"]
    }
}
