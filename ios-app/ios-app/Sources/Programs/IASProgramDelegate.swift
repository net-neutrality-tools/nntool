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
protocol IASProgramDelegate: class {

    func iasMeasurement(_ ias: IASProgram, didStartPhase phase: SpeedMeasurementPhase)

    func iasMeasurement(_ ias: IASProgram, didFinishPhase phase: SpeedMeasurementPhase)

    func iasMeasurement(_ ias: IASProgram, didMeasurePrimaryValue value: Double, inPhase phase: SpeedMeasurementPhase)

    func iasMeasurement(_ ias: IASProgram, didUpdateProgress progress: Double, inPhase phase: SpeedMeasurementPhase)
}
