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

///
public protocol MeasurementRunnerDelegate {

    /// Before control service measurement initiation request is sent
    func measurementWillStartRequestingControlModel(_ runner: MeasurementRunner)

    /// Measurement runner got LMAP control model from control service
    func measurementDidReceiveControlModel(_ runner: MeasurementRunner)

    /// Measurement runner did start
    func measurementDidStart(_ runner: MeasurementRunner)

    /// Measurement did finish successfully
    func measurementDidFinish(_ runner: MeasurementRunner, measurementUuid: String?, openDataUuid: String?)

    /// stopMeasurement() called by code
    func measurementDidStop(_ runner: MeasurementRunner)

    /// Measurement failed because of an error
    func measurementDidFail(_ runner: MeasurementRunner) // TODO: provide error

    func measurementRunner(_ runner: MeasurementRunner, willStartProgramWithName name: String, implementation: /*AnyProgram<Any>*/ProgramProtocol)

    func measurementRunner(_ runner: MeasurementRunner, didFinishProgramWithName name: String, implementation: /*AnyProgram<Any>*/ProgramProtocol)

}
