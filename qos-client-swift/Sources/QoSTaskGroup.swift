// TODO: license

import Foundation

typealias QoSTaskInitializer = (QoSTaskConfiguration) -> (QoSTask?)

// TODO: rewrite -> use factory?
// -> with "register type" approach?

public class QoSTaskGroup {

    private(set) public var key: String
    var localizedDescription: String

    class func groupForKey(_ key: String, localizedDescription desc: String) -> QoSTaskGroup? {
        return QoSTaskGroup(key: key, localizedDescription: desc)
    }

    init(key: String, localizedDescription desc: String) {
        self.key = key
        self.localizedDescription = desc
    }

    func taskWithConfiguration(config: QoSTaskConfiguration) -> QoSTask? {
        let task = TaskType(rawValue: key.lowercased())?.taskClass().create(config: config)
        task?.group = self

        return task
    }
}
