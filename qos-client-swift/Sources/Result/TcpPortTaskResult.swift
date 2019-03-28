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

///
public class TcpPortTaskResult: AbstractBidirectionalIpTaskResult {

    var resultOut: Status?
    var resultIn: Status?

    var resultOutResponse: String?
    var resultInResponse: String?

    var resultErrorDetails: String?

    ///
    public override func mapping(map: Map) {
        super.mapping(map: map)

        objectiveTimeoutNs   <- map["tcp_objective_timeout"] // TODO: use generic "objective_timeout"...

        objectivePortOut   <- map["tcp_objective_out_port"]
        objectivePortIn    <- map["tcp_objective_in_port"]

        resultOut          <- (map["tcp_result_out"], EnumTransform<Status>())
        resultIn           <- (map["tcp_result_in"], EnumTransform<Status>())

        resultOutResponse  <- map["tcp_result_out_response"]
        resultInResponse   <- map["tcp_result_in_response"]

        resultErrorDetails <- map["tcp_result_error_details"]
    }
}
