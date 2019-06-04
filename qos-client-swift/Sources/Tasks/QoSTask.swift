import Foundation
import CodableJSON

public typealias QoSTaskConfiguration = [String: JSON]
public typealias QoSTaskResult = [String: JSON]
public typealias QoSTaskCompletionCallback = (QoSTaskResult) -> Void

class QoSTask: Operation, Codable {

    let progress = Progress(totalUnitCount: 100)

    var status: QoSTaskStatus = .unknown

    var group: QoSTaskGroup?

    var uid: UInt
    var concurrencyGroup: UInt
    var type: String?

    var timeoutNs: UInt64 = 10 * NSEC_PER_SEC // default: 10sec
    var timeoutS: UInt64 {
        return timeoutNs / NSEC_PER_SEC
    }

    ////

    var startTimeNs: UInt64?
    var durationNs: UInt64?

    /// Override this property in a subclass to identify the key used for the status value.
    var statusKey: String { // TODO: unify this on the server side (use status instead of tcp_result_out, ...)
        return "status"
    }

    var result: QoSTaskResult {
        var r = QoSTaskResult()

        r["qos_test_uid"] = JSON(uid)
        r["test_type"] = JSON(type)
        r["duration_ns"] = JSON(durationNs)

        r[statusKey] = JSON(status.rawValue)

        return r
    }

    init?(config: QoSTaskConfiguration) {
        /*guard let uid = config[CodingKeys.uid.rawValue]?.uintValue, uid > 0 else {
            logger.debug("uid nil")
            return nil
        }*/
        guard let uidStr = config[CodingKeys.uid.rawValue]?.stringValue, let uid = UInt(uidStr), uid > 0 else {
            logger.debug("uid nil")
            return nil
        }

        guard let concurrencyGroup = config[CodingKeys.concurrencyGroup.rawValue]?.uintValue, concurrencyGroup > 0 else {
            logger.debug("concurrencyGroup nil")
            return nil
        }

        guard let type = config[CodingKeys.type.rawValue]?.stringValue else {
            logger.debug("type nil")
            return nil
        }

        self.uid = uid
        self.concurrencyGroup = concurrencyGroup
        self.type = type

        if let tNs = config[CodingKeys.timeout.rawValue]?.uint64Value {
            self.timeoutNs = tNs
        }

        super.init()
    }

// MARK: - Codeable

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        uid = try container.decode(UInt.self, forKey: .uid)
        concurrencyGroup = try container.decode(UInt.self, forKey: .concurrencyGroup)
        type = try container.decode(String.self, forKey: .type)
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

        // TODO: result
    }

// MARK: - Operation

    override func start() {
        // if isCancelled -> log

        // assert isFinished

        let startTimeNs = TimeHelper.currentTimeNs()
        self.startTimeNs = startTimeNs

        super.start()

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
