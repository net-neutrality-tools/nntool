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
public class UdpPortTaskConfiguration: AbstractBidirectionalIpTaskConfiguration {
    
    var packetCountOut: Int?
    var packetCountIn: Int?
    
    var delayNs: UInt64? // TODO: default?
    
    public override func mapping(map: Map) {
        super.mapping(map: map)
        
        packetCountOut <- map["out_num_packets"]
        packetCountIn <- map["in_num_packets"]
            
        delayNs <- map["delay"]
    }
}
