import XCGLogger

class QoSLogger: XCGLogger {

    private let task: QoSTask

    init(task: QoSTask) {
        self.task = task

        super.init()

        setup(level: logger.outputLevel, showLogIdentifier: true, showFunctionName: true, showThreadName: true, showLevel: true, showFileNames: true, showLineNumbers: true, showDate: true, writeToFile: nil, fileLevel: nil)
    }

    override func logln(_ level: XCGLogger.Level = .debug, functionName: String = #function, fileName: String = #file, lineNumber: Int = #line, userInfo: [String: Any] = [:], closure: () -> Any?) {

        super.logln(level, functionName: functionName, fileName: fileName, lineNumber: lineNumber, userInfo: userInfo) {
            guard let closureResult = closure() else { return nil }

            if let type = task.type {
                return "<\(type)|\(task.uid)|\(task.concurrencyGroup)> \(closureResult)"
            } else {
                return "<\(task.uid)|\(task.concurrencyGroup)> \(closureResult)"
            }
        }
    }
}
