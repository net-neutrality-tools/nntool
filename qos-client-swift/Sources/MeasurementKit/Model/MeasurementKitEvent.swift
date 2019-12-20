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

class MeasurementKitEvent: Codable {

    let key: String
    let value: Value

    var type: MeasurementKitEventType? {
        return MeasurementKitEventType(rawValue: key)
    }

    enum CodingKeys: String, CodingKey {
        case key
        case value
    }

    class Value: Codable {

        //let key: NSNumber?
        let status: String?
        let message: String?
        let percentage: Float?
        let jsonString: String?
        let idx: Int?

        let reportId: String?
        let probeIp: String?
        let probeAsn: String?
        let probeCc: String?
        let probeNetworkName: String?
        let downloadedKb: Double?
        let uploadedKb: Double?
        let input: String?
        let failure: String?

        enum CodingKeys: String, CodingKey {
            //case key
            case status
            case message
            case percentage
            case jsonString = "json_str"
            case idx

            case reportId = "report_id"
            case probeIp = "probe_ip"
            case probeAsn = "probe_asn"
            case probeCc = "probe_cc"
            case probeNetworkName = "probe_network_name"
            case downloadedKb = "downloaded_kb"
            case uploadedKb = "uploaded_kb"
            case input
            case failure
        }
    }
}
