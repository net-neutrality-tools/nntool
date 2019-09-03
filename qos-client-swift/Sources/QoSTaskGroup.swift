// TODO: license

import Foundation

typealias QoSTaskInitializer = (QoSTaskConfiguration) -> (QoSTask?)

// TODO: rewrite -> use factory?
// -> with "register type" approach?

public class QoSTaskGroup {

    private(set) public var key: String
    var localizedDescription: String

    private static let qosTypeDict = [
        "dns": DnsTask.self,
        "udp": UdpPortTask.self,
        "tcp": TcpPortTask.self,
        "echo_protocol": EchoProtocolTask.self,
        "http_proxy": HttpProxyTask.self,
        "traceroute": TracerouteTask.self,
        "website": WebsiteRenderingTask.self,
        "non_transparent_proxy": NonTransparentProxyTask.self,
        // MeasurementKit
        "mkit_web_connectivity": MeasurementKitTask.self,
        "mkit_dash": MeasurementKitTask.self
    ]

    class func groupForKey(_ key: String, localizedDescription desc: String) -> QoSTaskGroup? {
        return QoSTaskGroup(key: key, localizedDescription: desc)
    }

    init(key: String, localizedDescription desc: String) {
        self.key = key
        self.localizedDescription = desc
    }

    func taskWithConfiguration(config: QoSTaskConfiguration) -> QoSTask? {
        let task = QoSTaskGroup.qosTypeDict[key.lowercased()]?.create(config: config)
        task?.group = self

        return task
    }
}
