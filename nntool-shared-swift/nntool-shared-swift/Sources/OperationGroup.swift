/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import Foundation

/// An operation group that concurrently executes its children.
public class OperationGroup: Operation {
    
    ///
    let queue = OperationQueue()
    
    ///
    public let uid: UInt
    
    ///
    public var operations = [Operation]()
    
    /// Creates a operation group with a numeric uid.
    public init(uid: UInt) {
        self.uid = uid
        
        super.init()
        
        queue.maxConcurrentOperationCount = 100
    }
    
    /// Overrides main to add all children to the custom operation queue
    /// and waits until all child operations finish.
    override public func main() {
        //logger.debug("running operation group \(uid)")
        
        queue.addOperations(operations, waitUntilFinished: true)
        
        //logger.debug("finished operation group \(uid)")
    }
}
