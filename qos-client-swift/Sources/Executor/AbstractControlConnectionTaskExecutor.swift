// TODO: license

import Foundation

///
public class AbstractControlConnectionTaskExecutor<C: AbstractControlConnectionTaskConfiguration, R: AbstractControlConnectionTaskResult>: AbstractTaskExecutor<C, R> {

    ///
    private let controlConnection: ControlConnection
    
    ///
    public init(config: C, controlConnection: ControlConnection) {
        self.controlConnection = controlConnection
        
        super.init(config: config)
    }
    
    ///
    func executeCommand(cmd: String, waitForAnswer: Bool = false) throws -> String? {
        let semaphore = DispatchSemaphore(value: 0)
        
        var response: String?
        var error: Error?
        
        controlConnection.executeCommand(cmd: cmd, waitForAnswer: waitForAnswer, success: { r in
            response = r
            
            semaphore.signal()
        }) { err in
            error = err
            
            semaphore.signal()
        }
        
        //semaphore.wait(timeout: .now() + .nanoseconds(Int(config?.timeoutNs)))
        /*if let timeout = internalConfig.timeoutNs {
             _ = semaphore.wait(timeout: .now() + .nanoseconds(Int(timeout))) // TODO: if timeout throw error?
        } else {*/
            _ = semaphore.wait(timeout: .distantFuture) // TODO: if timeout throw error?
        //}
        
        if let err = error {
            throw err
        }
        
        return response
    }
    
    ///
    func extractUuidFromToken() -> String {
        assert(controlConnection.token.contains("_")) // TODO: enforce token structure on control connection init
        
        return controlConnection.token.components(separatedBy: "_").first! // !
    }
}
