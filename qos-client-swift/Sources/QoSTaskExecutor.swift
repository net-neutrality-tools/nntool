// TODO: license

import Foundation
import nntool_shared_swift

public typealias QoSObjectives = [String: [QoSTaskConfiguration]]

public class QoSTaskExecutor {

    private let notificationQueue = DispatchQueue(label: "at.alladin.nettest.berec.qoskit.notification")

    private let queue: OperationQueue

    public weak var delegate: QoSTaskExecutorDelegate?

    private var controlConnections = [ControlConnectionParameters: ControlConnection]()

    private var totalProgress: Progress?

    private var tasks = [QoSTask]()
    private var groups = [QoSTaskGroup]()
    private var groupsProgress = [Progress]()

    private var progressObservers = [NSKeyValueObservation]()

    var token: String?

    enum QoSTaskExecutorError: Error {
        case noObjectives
    }

    ////

    public init() {
        queue = OperationQueue()
        queue.isSuspended = true
        queue.maxConcurrentOperationCount = 100 // 4?
    }

    public class func startWithObjectives(_ objectives: QoSObjectives, token: String? = "", success successCallback: (([QoSTaskResult]) -> Void)?, error errorCallback: ((Error?) -> Void)?) {

        let executor = CallbackQoSTaskExecutor(successCallback: successCallback, errorCallback: errorCallback)
        executor.startWithObjectives(objectives, token: token)
    }

    public func startWithObjectives(_ objectives: QoSObjectives, token: String? = "") {
        self.token = token

        logger.debug(objectives)

        /*if objectives.isEmpty { // if there are no tasks finish with empty result
            delegate.taskExecutorDidFinishWithResult([QoSTaskResult]())
            return
        }*/

        objectives.forEach { item in
            let (key, taskConfigurations) = item

            // TODO: refactor qos task creation into factory
            guard let group = QoSTaskGroup.groupForKey(key) else {
                return
            }

            var groupsTasksProgress = [Progress]()

            taskConfigurations.forEach { config in
                guard let task = group.taskWithConfiguration(config: config) else {
                    return
                }

                tasks.append(task)
                groupsTasksProgress.append(task.progress)
            }

            if !groupsTasksProgress.isEmpty {
                groups.append(group)

                let groupProgress = Progress(totalUnitCount: groupsTasksProgress.reduce(0, { $0 + $1.totalUnitCount }))
                groupsTasksProgress.forEach { groupProgress.addChild($0, withPendingUnitCount: $0.totalUnitCount) }

                progressObservers.append(groupProgress.observe(\.fractionCompleted, options: .new, changeHandler: { (p, _) in
                    self.notificationQueue.async {
                        logger.debug("progress change handler: \(p.fractionCompleted), \(group.key), \(self.totalProgress?.fractionCompleted ?? 0)")
                        self.delegate?.taskExecutorDidUpdateProgress(p.fractionCompleted, ofGroup: group, totalProgress: self.totalProgress?.fractionCompleted ?? 0)
                    }
                }))

                groupsProgress.append(groupProgress)
            }
        }

        logger.debug(tasks)

        if tasks.isEmpty { // Fail the measurement if there are no tasks
            fail(error: QoSTaskExecutorError.noObjectives)
            return
        }

        totalProgress = Progress(totalUnitCount: groupsProgress.reduce(0, { $0 + $1.totalUnitCount }))
        groupsProgress.forEach { totalProgress?.addChild($0, withPendingUnitCount: $0.totalUnitCount) }

        createControlConnections(forTasks: tasks)

        delegate?.taskExecutorDidStart(self, withTaskGroups: groups)

        let concurrencyGroups = buildExecutionPlan()
        enqueue(concurrencyGroups: concurrencyGroups)
    }

    /// create control connection objects; reuse existing ones
    private func createControlConnections(forTasks tasks: [QoSTask]) {
        controlConnections = tasks.reduce(into: controlConnections, { (result, task) in
            if let t = task as? RequireControlConnection {
                let params = t.controlConnectionParams

                var conn = result[params]
                if conn == nil {
                    conn = ControlConnection(host: params.host, port: params.port, timeoutS: 5, token: token ?? "")
                    result[params] = conn
                }

                t.controlConnection = conn
            }
        })
    }

    private func buildExecutionPlan() -> [OperationGroup] {
        // reduce list of tasks into a dictionary where key equals concurrencyGroup
        let byGroup = tasks.reduce(into: [UInt: [QoSTask]]()) { (result, task) in
            var taskArray = result[task.concurrencyGroup]
            if taskArray == nil {
                taskArray = [QoSTask]()
                result[task.concurrencyGroup] = taskArray
            }

            result[task.concurrencyGroup]?.append(task)
        }

        // get a sorted list of concurrencyGroups
        let sortedKeysByGroup = Array(byGroup.keys).sorted(by: <)

        // map concurrencyGroup to QoSTaskOperationConcurrencyGroup
        let groups = sortedKeysByGroup.map { concurrencyGroup -> OperationGroup in
            let group = OperationGroup(uid: concurrencyGroup)

            if let t = byGroup[concurrencyGroup] {
                group.operations.append(contentsOf: t)
            }

            return group
        }

        logger.debugExec {
            logger.debug("---------")
            logger.debug("QoS execution plan")
            for group in groups {
                logger.debug("Group \(group.uid):")
                for task in group.operations {
                    if let t = task as? QoSTask {
                        logger.debug("  - \(String(describing: t.type)) \(t.uid)") // TODO: refactor description method
                    }
                }
            }
            logger.debug("---------")
        }

        return groups
    }

    private func enqueue(concurrencyGroups groups: [OperationGroup]) {
        // enqueue concurrency groups, set each dependency to previous one
        var prevGroup: OperationGroup?
        for group in groups {
            if let p = prevGroup {
                group.addDependency(p)
            }

            queue.addOperation(group)

            prevGroup = group
        }

        // enqueue finish operation
        let finishOperation = BlockOperation {
            //self.stopObservingProgress()

            // gather task results
            let results = self.tasks.map { $0.result }

            self.notificationQueue.async {
                self.delegate?.taskExecutorDidFinishWithResult(results)
                self.stop()
            }
        }

        if let p = prevGroup {
            finishOperation.addDependency(p)
        }

        queue.addOperation(finishOperation)

        WebsiteTaskUrlProtocol.start()

        // start queue
        queue.isSuspended = false
    }

    ////

    private func fail(error: Error? = nil) {
        delegate?.taskExecutorDidFail(self, withError: error)
        cancel()
    }

    public func cancel() {
        queue.cancelAllOperations()
        stop()
    }

    private func stop() {
        WebsiteTaskUrlProtocol.stop()

        stopObservingProgress()
        closeAllControlConnections()

        delegate?.taskExecutorDidStop(self)
        // TODO: reset()
    }

    private func stopObservingProgress() {
        progressObservers.forEach { $0.invalidate() } // invalidate all observers
        progressObservers.removeAll()
    }

    private func closeAllControlConnections() {
        controlConnections.values.forEach { $0.disconnect() }
        controlConnections.removeAll()
    }
}

private class CallbackQoSTaskExecutor: QoSTaskExecutor, QoSTaskExecutorDelegate {

    private let successCallback: (([QoSTaskResult]) -> Void)?
    private let errorCallback: ((Error?) -> Void)?

    init(successCallback: (([QoSTaskResult]) -> Void)?, errorCallback: ((Error?) -> Void)?) {
        self.successCallback = successCallback
        self.errorCallback = errorCallback

        super.init()

        delegate = self
    }

    func taskExecutorDidStart(_ taskExecutor: QoSTaskExecutor, withTaskGroups groups: [QoSTaskGroup]) {

    }

    func taskExecutorDidFail(_ taskExecutor: QoSTaskExecutor, withError error: Error?) {
        errorCallback?(error)
    }

    func taskExecutorDidStop(_ taskExecutor: QoSTaskExecutor) {

    }

    func taskExecutorDidUpdateProgress(_ progress: Double, ofGroup group: QoSTaskGroup, totalProgress: Double) {

    }

    func taskExecutorDidFinishWithResult(_ result: [QoSTaskResult]) {
        successCallback?(result)
    }
}
