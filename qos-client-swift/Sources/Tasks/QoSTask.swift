import Foundation
import CodableJSON
import nntool_shared_swift

public typealias QoSTaskConfiguration = [String: JSON]
public typealias QoSTaskResult = [String: JSON]
public typealias QoSTaskCompletionCallback = (QoSTaskResult) -> Void

class QoSTask: Operation, Codable {

    enum ParseError: Error {
        case parseError(String)
    }
    
    var taskLogger: QoSLogger!

    let progress = Progress(totalUnitCount: 100)

    var status: QoSTaskStatus = .unknown

    var group: QoSTaskGroup?

    var uid: UInt
    var concurrencyGroup: UInt
    var type: String?

    var timeoutNs: UInt64 = 3 * NSEC_PER_SEC // default: 3sec
    var timeoutS: UInt64 {
        return timeoutNs / NSEC_PER_SEC
    }

    ////

    var startTimeNs: UInt64?
    var durationNs: UInt64?

    /// Override this property in a subclass to identify the key used for the status value.
    var statusKey: String? { // TODO: unify this on the server side (use status instead of tcp_result_out, ...)
        return nil
    }

    var objectiveTimeoutKey: String? { // TODO: unify this on the server side
        return nil
    }

    var result: QoSTaskResult {
        var r = QoSTaskResult()

        r["qos_test_uid"] = JSON(uid)
        r["test_type"] = JSON(type)
        r["duration_ns"] = JSON(durationNs)

        if let statusKey = statusKey {
            r[statusKey] = JSON(status.rawValue)
        }

        if let objectiveTimeoutKey = objectiveTimeoutKey {
            r[objectiveTimeoutKey] = JSON(timeoutNs)
        }

        return r
    }

    class func create(config: QoSTaskConfiguration) -> Self? {
        do {
            let configData = try JSONEncoder().encode(config)
            let task = try JSONDecoder().decode(self, from: configData)

            return task
        } catch {
            logger.error(error)
            return nil
        }
    }

// MARK: - Codeable

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        if let uid = try? UInt(container.decode(String.self, forKey: .uid)) {
            self.uid = uid
        } else {
            uid = try container.decode(UInt.self, forKey: .uid)
        }

        concurrencyGroup = try container.decode(UInt.self, forKey: .concurrencyGroup)
        type = try container.decode(String.self, forKey: .type)

        super.init()

        taskLogger = QoSLogger(task: self)
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)

        try container.encode(uid, forKey: .uid)
    }

    ///
    enum CodingKeys: String, CodingKey {
        case uid = "qos_test_uid"
        case concurrencyGroup = "concurrency_group"
        case type = "qostest"
        case timeout = "timeout"
    }

// MARK: - Operation

    func taskMain() {
        fatalError("taskMain must be overriden in subclasses of QoSTask!")
    }

    override func main() {
        let startTimeNs = TimeHelper.currentTimeNs()
        self.startTimeNs = startTimeNs

        taskMain()

        durationNs = TimeHelper.currentTimeNs() - startTimeNs

        // set progress to 100%
        progress.completedUnitCount = progress.totalUnitCount
    }
}

// MARK: - Run Single Task

extension QoSTask {

    ///
    public func runTask(completion: QoSTaskCompletionCallback?) {
        DispatchQueue.global(qos: .background).async {
            let result = self.runTask()
            completion?(result)
        }
    }

    ///
    public func runTask() -> QoSTaskResult {
        let operationQueue = OperationQueue()

        operationQueue.addOperation(self)
        operationQueue.waitUntilAllOperationsAreFinished()

        return result
    }
}

// MARK: - ...StringConvertable

extension QoSTask {

    /*override var description: String {
     
     }
     
     override var debugDescription: String {
     
     }*/
}

// MARK: - Comparable

extension QoSTask: Comparable {
    static func < (lhs: QoSTask, rhs: QoSTask) -> Bool {
        return lhs.concurrencyGroup < rhs.concurrencyGroup
    }
}
