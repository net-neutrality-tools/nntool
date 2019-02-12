// TODO: license
// inspired by https://github.com/rtr-nettest/open-rmbt-ios/blob/master/Sources/RMBTQoSControlConnection.m

import Foundation
import CocoaAsyncSocket

///
public class ControlConnection: NSObject {
    
    typealias SuccessCallback = (_ response: String?) -> ()
    typealias ErrorCallback = (_ error: Error) -> ()
    
    public enum State: Int {
        case disconnecting
        case disconnected
        case connecting
        case connected
        case authenticating
        case authenticated
    }
    
    public enum Tag: Int {
        case greeting
        case accept
        case token
        case acceptAfterToken
        case command
        case quit
    }
    
    private let delegateQueue = DispatchQueue(label: "at.alladin.nettest.qos.control.delegate")
    private let commandQueue = DispatchQueue(label: "at.alladin.nettest.qos.control.command")
    
    private var socket: GCDAsyncSocketProtocol
    
    private(set) var state: State = .disconnected

    private var currentCommand: String?
    private var currentCommandReadAnswer: Bool = false
    
    private var currentSuccessCallback: SuccessCallback?
    private var currentErrorCallback: ErrorCallback?
    
    ////
    
    private let host: String
    private let port: UInt16
    private let tls: Bool
    private let timeoutS: Double
    let token: String/*?*/ // TODO: make token optional
    
    public init(host: String, port: UInt16, tls: Bool = true, timeoutS: Double, token: String, socket: GCDAsyncSocketProtocol = GCDAsyncSocket() /* testability */) {
        // TODO: GCDAsyncSocketProtocol
        
        self.host = host
        self.port = port
        self.timeoutS = timeoutS
        self.token = token
        self.tls = tls
    
        self.socket = socket
        
        super.init()
        
        self.socket.delegate = self
        self.socket.delegateQueue = delegateQueue
    }
    
    ///
    func connect() {
        assert(state == .disconnected)
        
        state = .connecting
        
        do {
            try socket.connect(toHost: host, onPort: port, withTimeout: timeoutS)
        } catch {
            state = .disconnected

            finishCurrentCommand(response: nil, error: error)
        }
    }

    ///
    func disconnect() {
        state = .disconnecting
        
        if socket.isConnected {
            writeLine(line: "QUIT", tag: .quit, readAnswer: false)
        }
        
        socket.disconnect()
    }
    
    ///
    func executeCommand(cmd: String, waitForAnswer: Bool = false, success successCallback: SuccessCallback?, error errorCallback: ErrorCallback?) {
        commandQueue.async {
            self.commandQueue.suspend()
            
            self.currentCommand = cmd
            self.currentCommandReadAnswer = waitForAnswer
            self.currentSuccessCallback = successCallback
            self.currentErrorCallback = errorCallback
            
            if self.state == .disconnected {
            //if self.state != .authenticated {
                self.connect()
            } else {
                assert(self.state == .authenticated)
                self.writeCurrentCommand()
            }
        }
    }
    
    ///
    private func writeCurrentCommand() {
        if let cmd = currentCommand {
            writeLine(line: cmd, tag: .command, readAnswer: currentCommandReadAnswer)
        }
    }
    
    ///
    private func finishCurrentCommand(response: String?, error: Error?) {
        if let err = error {
            currentErrorCallback?(err)
        } else {
            currentSuccessCallback?(response)
        }
        
        currentCommand = nil
        currentSuccessCallback = nil
        currentErrorCallback = nil
        
        commandQueue.resume()
    }
    
    ///
    private func writeLine(line: String, tag: Tag, readAnswer: Bool = false) {
        if let requestData = line.appending("\n").data(using: .utf8) {
            logger.debug("write: '\(line)'")
            socket.write(requestData, withTimeout: timeoutS, tag: tag.rawValue)
            
            if readAnswer {
                readLine(tag: tag)
            }
        }
    }
    
    ///
    private func readLine(tag: Tag) {
        logger.debug("read tag: \(tag)")
        socket.readData(to: GCDAsyncSocket.lfData(), withTimeout: timeoutS, tag: tag.rawValue)
    }
}

///
extension ControlConnection: GCDAsyncSocketDelegate {
    
    ///
    public func socket(_ sock: GCDAsyncSocket, didConnectToHost host: String, port: UInt16) {
        if tls {
            sock.startTLS([
                GCDAsyncSocketManuallyEvaluateTrust: NSNumber(value: true)
            ])
        } else {
            readLine(tag: .greeting)
        }
    }
    
    public func socket(_ sock: GCDAsyncSocket, didReceive trust: SecTrust, completionHandler: @escaping (Bool) -> Void) {
        // wrong error message if this method isn't implemented:
        // "GCDAsyncSocketManuallyEvaluateTrust specified in tlsSettings, but delegate doesn't implement socket:shouldTrustPeer:"
        
        completionHandler(true) // TODO: make this configurable
    }
    
    ///
    public func socketDidSecure(_ sock: GCDAsyncSocket) {
        readLine(tag: .greeting)
    }
    
    ///
    public func socket(_ sock: GCDAsyncSocket, didRead data: Data, withTag tag: Int) {
        logger.debug("!!!!!!!!! \(String(describing: String(data: data, encoding: .utf8)))")
        
        if let t = Tag(rawValue: tag) {
            switch t {
            case .greeting:
                readLine(tag: .accept)
            case .accept:
                state = .authenticating
                writeLine(line: "TOKEN \(token)", tag: .token, readAnswer: true)
            case .token:
                readLine(tag: .acceptAfterToken)
            case .acceptAfterToken:
                state = .authenticated
                writeCurrentCommand()
            case .command:
                let line = String(data: data, encoding: .utf8)
                finishCurrentCommand(response: line, error: nil)
            case .quit:
                break
            }
        } else {
            assert(false) // should never happen
            
            disconnect()
        }
    }
    
    ///
    public func socket(_ sock: GCDAsyncSocket, didWriteDataWithTag tag: Int) {
        if let t = Tag(rawValue: tag) {
            if t == .command && !currentCommandReadAnswer {
                finishCurrentCommand(response: nil, error: nil)
            }
        }
    }
    
    ///
    public func socketDidDisconnect(_ sock: GCDAsyncSocket, withError err: Error?) {
        state = .disconnected
        
        if currentErrorCallback != nil {
            finishCurrentCommand(response: nil, error: err)
        }
    }
}
