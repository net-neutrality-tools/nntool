// TODO: license

import Foundation

typealias QoSTaskInitializer = (QoSTaskConfiguration) -> (QoSTask?)

// TODO: rewrite -> use factory?
// -> with "register type" approach?

public class QoSTaskGroup {

    private(set) public var key: String
    var localizedDescription: String
    var initializer: QoSTaskInitializer

    class func groupForKey(_ key: String, localizedDescription desc: String) -> QoSTaskGroup? {
        var initializer: QoSTaskInitializer

        switch key.lowercased() {
        case "dns": initializer = { DnsTask(config: $0) }
        case "udp": initializer = { UdpPortTask(config: $0) }
        case "tcp": initializer = { TcpPortTask(config: $0) }
        case "echo_protocol": initializer = { EchoProtocolTask(config: $0) }
            /*case "http_proxy":
             initializer = {
             return QoSHttpProxyTest(params: $0)
             }
             case "traceroute":
             initializer = {
             return QoSTracerouteTest(params: $0)
             }
             case "website":
             initializer = {
             return QoSWebsiteRenderingTest(params: $0)
             }
             case "udp":
             initializer = {
             return QoSUdpTest(params: $0)
             }
             case "tcp":
             initializer = {
             return QoSTcpTest(params: $0)
             }
             case "non_transparent_proxy":
             initializer = {
             return QoSNonTransparentProxyTest(params: $0)
             }*/
        default:
            return nil
        }

        return QoSTaskGroup(key: key, localizedDescription: desc, initializer: initializer)
    }

    init(key: String, localizedDescription desc: String, initializer: @escaping QoSTaskInitializer) {
        self.key = key
        self.localizedDescription = desc
        self.initializer = initializer
    }

    func taskWithConfiguration(config: QoSTaskConfiguration) -> QoSTask? {
        let task = initializer(config)
        task?.group = self

        return task
    }
}
