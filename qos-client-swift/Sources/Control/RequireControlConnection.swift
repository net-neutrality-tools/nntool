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
    func executeCommand(cmd: String, waitForAnswer: Bool) throws -> String?
}
