// ios-app: QoSProgram.swift, created on 11.04.19
/*******************************************************************************
 * Copyright 2019 Benjamin Pucher (alladin-IT GmbH)
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
import MeasurementAgentKit
import QoSKit

///
class QoSProgram: ProgramProtocol {
    //typealias Delegate = QoSProgramDelegate

    //var delegate: /*QoSProgramDelegate*/Any?

    func run() throws -> [AnyHashable: Any] {
        print("----\nRUN QOS PROGRAM\n----")
        sleep(5)
        print("----\nQOS PROGRAM finished\n----")
        
        return [:]
    }
}
