// TODO: license

import Foundation

typealias QoSTaskInitializer = (QoSTaskConfiguration) -> (QoSTask?)

// TODO: rewrite -> use factory?
// -> with "register type" approach?

public class QoSTaskGroup {

    private(set) public var key: String

    class func groupForKey(_ key: String) -> QoSTaskGroup? {
        return QoSTaskGroup(key: key)
    }

    init(key: String) {
        self.key = key
    }

    func taskWithConfiguration(config: QoSTaskConfiguration) -> QoSTask? {
        let task = TaskType(rawValue: key.lowercased())?.taskClass().create(config: config)
        task?.group = self

        return task
    }
}
