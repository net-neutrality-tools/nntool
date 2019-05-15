// MeasurementAgentKit: LmapStateDto.swift, created on 28.03.19
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

/// TODO
enum LmapStateDto: String, Codable {

    /// The value 'enabled' indicates that the Action/Schedule is currently enabled.
    case enabled = "ENABLED"

    /// The value 'disabled' indicates that the Action/Schedule is currently disabled.
    case disabled = "DISABLED"

    /// The value 'running' indicates that the Action/Schedule is currently running.
    case running = "RUNNING"

    /// The value 'suppressed' indicates that the Action/Schedule is currently suppressed.
    case suppressed = "SUPPRESSED"
}
