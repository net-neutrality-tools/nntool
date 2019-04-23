// ios-app: Config.swift, created on 15.04.19
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

let MEASUREMENT_AGENT =
    MeasurementAgentBuilder(controlServiceBaseUrl: "http://localhost:8080/api/v1")
        .program(task: .speed, config: ProgramConfiguration(name: "IAS", version: "1.0.0") { (_: LmapTaskDto) in
            // TODO: use configuration of provided object

            return /*AnyProgram<IASProgramDelegate>(*/IASProgram()/*)*/
        })
        .program(task: .qos, config: ProgramConfiguration(name: "QOS", version: "1.0.0") { (_: LmapTaskDto) in
            // TODO: use configuration of provided object

            return QoSProgram()
        })
        .build()

////

let BEREC_WHITE      = UIColor.white
let BEREC_LIGHT_GRAY = UIColor(rgb: 0xEFEFEF)
let BEREC_GRAY       = UIColor(rgb: 0xD0D0D0)
let BEREC_BLUE       = UIColor(rgb: 0x8C94A9)
let BEREC_DARK_GRAY  = UIColor(rgb: 0x4D515D)
let BEREC_DARK_BLUE  = UIColor(rgb: 0x29348A)
let BEREC_RED        = UIColor(rgb: 0x921F56)

////

let APP_TINT_COLOR = BEREC_DARK_GRAY

