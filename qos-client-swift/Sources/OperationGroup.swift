import Foundation

/// An operation group that concurrently executes its children.
class OperationGroup: Operation {

    ///
    let queue = OperationQueue()

    ///
    let uid: UInt

    ///
    var operations = [Operation]()

    /// Creates a operation group with a numeric uid.
    init(uid: UInt) {
        self.uid = uid

        super.init()

        queue.maxConcurrentOperationCount = 100
    }

    /// Overrides main to add all children to the custom operation queue
    /// and waits until all child operations finish.
    override func main() {
        logger.debug("running operation group \(uid)")

        queue.addOperations(operations, waitUntilFinished: true)

        logger.debug("finished operation group \(uid)")
    }
}
