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
import CodableJSON

let MEASUREMENT_AGENT =
    MeasurementAgentBuilder(controlServiceBaseUrl: "http://localhost:18080/api/v1")
        .program(task: .speed, config: ProgramConfiguration(name: "IAS", version: "1.0.0") { (taskDto: LmapTaskDto) in
            let optionDict = taskDto.options?.reduce(into: [String: Any]()) { result, option in
                guard let name = option.name else {
                    return
                }

                if let mp = option.measurementParameters {
                    result[name] = mp
                } else {
                    result[name] = option.value
                }
            }

            let measurementConfig = ((optionDict?["parameters_speed"] as? MeasurementTypeParametersWrapperDto)?.content as? IasMeasurementTypeParametersDto)?.measurementConfiguration

            let p = IASProgram()
            p.config = measurementConfig

            return p
        })
        .program(task: .qos, config: ProgramConfiguration(name: "QOS", version: "1.0.0") { (taskDto: LmapTaskDto) in
            let optionDict = taskDto.options?.reduce(into: [String: Any]()) { result, option in
                guard let name = option.name else {
                    return
                }

                if let mp = option.measurementParameters {
                    result[name] = mp
                } else {
                    result[name] = option.value
                }
            }

            let p = QoSProgram()

            if var objectives: [String: [[String: JSON]]] = ((optionDict?["parameters_qos"] as? MeasurementTypeParametersWrapperDto)?.content as? QoSMeasurementTypeParametersDto)?.objectives {

                if let serverAddr = optionDict?["server_addr"] as? String, let serverPort = optionDict?["server_port"] as? String {

                    for item in objectives {
                        let (k, v) = item

                        for var (index, i) in v.enumerated() {
                            if i["server_addr"] == nil || i["server_port"] == nil {
                                objectives[k]?[index]["server_addr"] = JSON(serverAddr)
                                objectives[k]?[index]["server_port"] = JSON(serverPort)
                            }
                        }
                    }
                }

                p.objectives = objectives
            }

            return p
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

////

let MEASUREMENT_TRAFFIC_WARNING_ENABLED = false
