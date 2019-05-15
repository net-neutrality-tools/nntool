import Foundation

protocol RequireControlConnection: QoSTask {

    var controlConnectionParams: ControlConnectionParameters {
        get
    }

    ///
    var controlConnection: ControlConnection? {
        get set
    }

    ///
    func executeCommand(cmd: String, waitForAnswer: Bool, timeoutNs: UInt64) throws -> String?
}
