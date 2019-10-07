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
