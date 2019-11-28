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

let MEASUREMENT_AGENT_CONTROLLER_BASE_URL = "https://controller-de-01.net-neutrality.tools/api/v1"
/*"http://localhost:18080/api/v1"*/

let MEASUREMENT_AGENT =
    MeasurementAgentBuilder(controllerServiceBaseUrl: MEASUREMENT_AGENT_CONTROLLER_BASE_URL)
        .program(
            task: .speed,
            config: ProgramConfiguration(
                name: "IAS",
                version: "1.0.0",
                isEnabled: true,
                availableTasks: [
                    ProgramTask(
                        name: "rtt",
                        localizedName: R.string.localizable.measurementSpeedPhaseRtt(),
                        localizedDescription: R.string.localizable.measurementSpeedPhaseRttDescription()
                    ),
                    ProgramTask(
                        name: "download",
                        localizedName: R.string.localizable.measurementSpeedPhaseDownload(),
                        localizedDescription: R.string.localizable.measurementSpeedPhaseDownloadDescription()
                    ),
                    ProgramTask(
                        name: "upload",
                        localizedName: R.string.localizable.measurementSpeedPhaseUpload(),
                        localizedDescription: R.string.localizable.measurementSpeedPhaseUploadDescription()
                    )
                ]
            ) { (taskDto: LmapTaskDto, programConfiguration: ProgramConfiguration) in
            let p = IASProgram()

            p.programConfiguration = programConfiguration

            if let measurementConfig = (taskDto.getMeasurementParametersByName("parameters_speed")?.content as? IasMeasurementTypeParametersDto)?.measurementConfiguration {

                p.config = measurementConfig
            }

            if let serverAddr = taskDto.getOptionByName(/*"server_addr"*/"server_addr_default"), let serverPort = taskDto.getOptionByName("server_port") {
                p.serverAddress = serverAddr
                p.serverPort = serverPort

                if let encryptionStr = taskDto.getOptionByName("encryption") {
                    p.encryption = Bool(encryptionStr) ?? false
                }
            }

            return p
        })
        .program(
            task: .qos,
            config: ProgramConfiguration(
                name: "QOS",
                version: "1.0.0",
                isEnabled: true,
                availableTasks: QoSMeasurementType.allCases.map { ProgramTask(name: $0.rawValue.lowercased()) }
            ) { (taskDto: LmapTaskDto, programConfiguration: ProgramConfiguration) in
            let p = QoSProgram()

            if var objectives = (taskDto.getMeasurementParametersByName("parameters_qos")?.content as? QoSMeasurementTypeParametersDto)?.objectives {

                if let serverAddr = taskDto.getOptionByName("server_addr"), let serverPort = taskDto.getOptionByName("server_port") {

                    for item in objectives {
                        let (k, v) = item

                        guard programConfiguration.enabledTasks.contains(k.lowercased()) else {
                            logger.debug("ignoring \(k) -> '\(v)' because it is not enabled")
                            objectives.removeValue(forKey: k)
                            continue
                        }

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

// GoogleMaps API key is required. The app will crash on the map view if no key is provided.
let GOOGLE_MAPS_API_KEY = ""

let MAP_VIEW_INITIAL_LOCATION_LATITUDE = 47.493910
let MAP_VIEW_INITIAL_LOCATION_LONGITUDE = 11.644471
let MAP_VIEW_INITIAL_ZOOM = 4

let MAP_VIEW_POINT_LAYER_ZOOM_THRESHOLD = 12

////

let STATISTICS_URL = "https://nntool.net-neutrality.tools/statistics"
let HELP_URL = "https://nntool.net-neutrality.tools/help"

let INFO_WEBSITE_URL = "https://nntool.net-neutrality.tools"
let INFO_EMAIL = "todo@nntool.eu"

////

let BEREC_WHITE      = UIColor.white
let BEREC_LIGHT_GRAY = UIColor(rgb: 0xEFEFEF)
let BEREC_GRAY       = UIColor(rgb: 0xD0D0D0)
let BEREC_BLUE       = UIColor(rgb: 0x8C94A9)
let BEREC_DARK_GRAY  = UIColor(rgb: 0x4D515D)
let BEREC_DARK_BLUE  = UIColor(rgb: 0x29348A)
let BEREC_RED        = UIColor(rgb: 0x921F56)

let COLOR_CHECKMARK_GREEN = UIColor(rgb: 0x30C80B)
let COLOR_CHECKMARK_YELLOW = UIColor(rgb: 0xFECB1D)
let COLOR_CHECKMARK_RED = UIColor.red
let COLOR_CHECKMARK_DARK_GRAY = BEREC_DARK_GRAY

////

let APP_TINT_COLOR = BEREC_DARK_GRAY

////

let MEASUREMENT_TRAFFIC_WARNING_ENABLED = false
