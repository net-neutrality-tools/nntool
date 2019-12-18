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
import CodableJSON
import CocoaAsyncSocket

class WebsiteRenderingTask: QoSTask {

    private var url: String

    private var protocolResult: WebsiteTaskResultEntry?
    private var renderDurationNs: UInt64?

    ///
    enum CodingKeys4: String, CodingKey {
        case url
    }

    override var statusKey: String? {
        return "website_result_info"
    }

    override var objectiveTimeoutKey: String? {
        return "timeout"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["website_objective_url"] = JSON(url)

        r["website_result_duration"] = JSON(renderDurationNs)

        r["website_result_status"] = JSON(protocolResult?.status)
        r["website_result_rx_bytes"] = JSON(protocolResult?.rx)
        //r["website_result_tx_bytes"] = JSON() // not supported on iOS?

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        url = try container.decode(String.self, forKey: .url)

        try super.init(from: decoder)
    }

    ///
    override public func taskMain() {
        guard let url = URL(string: url) else {
            status = .error
            return
        }

        let websiteTaskRunner = WebsiteTaskRunner(uid: uid)
        (protocolResult, renderDurationNs, status) = websiteTaskRunner.run(urlObj: url, timeout: timeoutNs)
    }
}
