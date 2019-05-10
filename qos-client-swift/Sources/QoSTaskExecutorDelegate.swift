import Foundation

public protocol QoSTaskExecutorDelegate: AnyObject {

    func taskExecutorDidStart(_ taskExecutor: QoSTaskExecutor, withTaskGroups groups: [QoSTaskGroup])

    func taskExecutorDidFail(_ taskExecutor: QoSTaskExecutor, withError error: Error?)

    func taskExecutorDidUpdateProgress(_ progress: Double, ofGroup group: QoSTaskGroup, totalProgress: Double)

    func taskExecutorDidFinishWithResult(_ result: [QoSTaskResult])
}
