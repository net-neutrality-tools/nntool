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
        return "website_objective_timeout"
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

    ///
    override init?(config: QoSTaskConfiguration) {
        guard let url = config[CodingKeys4.url.rawValue]?.stringValue else {
            logger.debug("url nil")
            return nil
        }

        self.url = url

        super.init(config: config)
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        url = try container.decode(String.self, forKey: .url)

        try super.init(from: decoder)
    }

    ///
    override public func main() {
        guard let url = URL(string: url) else {
            status = .error
            return
        }

        let websiteTaskRunner = WebsiteTaskRunner(uid: uid)
        (protocolResult, renderDurationNs, status) = websiteTaskRunner.run(urlObj: url, timeout: timeoutNs)
    }
}