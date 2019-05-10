/***************************************************************************
 * Copyright 2018-2019 alladin-IT GmbH
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
 ***************************************************************************/

import Foundation

public class AbstractTaskConfigurationBuilder<C: AbstractTaskConfiguration>: BuilderProtocol {
    typealias Result = C

    var obj = C()

    public init() {

    }

    ////

    @discardableResult
    public func timeoutNs(_ timeoutNs: UInt64) -> Self {
        obj.timeoutNs = timeoutNs

        return self
    }

    @discardableResult
    public func qosTestUid(_ qosTestUid: Int) -> Self {
        obj.qosTestUid = qosTestUid

        return self
    }

    @discardableResult
    public func concurrencyGroup(_ concurrencyGroup: Int) -> Self {
        obj.concurrencyGroup = concurrencyGroup

        return self
    }

    public func build() -> C {
        return obj
    }
}
