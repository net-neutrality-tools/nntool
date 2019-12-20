/***************************************************************************
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
 ***************************************************************************/

import Foundation

enum MeasurementKitEventType: String {
    case statusQueued = "status.queued"
    case statusStarted = "status.started"
    case statusMeasurementStart = "status.measurement_start"
    case statusMeasurementDone = "status.measurement_done"
    case statusMeasurementSubmission = "status.measurement_submission"
    case statusEnd = "status.end"
    case statusReportCreate = "status.report_create"

    case statusUpdateWebsites = "status.update.websites"

    case failureStartup = "failure.startup"
    case failureMeasurementSubmission = "failure.measurement_submission"
    case failureReportCreate = "failure.report_create"

    case measurement// = "measurement"
    case progress = "status.progress"
    case geoIpLookup = "status.geoip_lookup"
    case log// = "log"

    case bugJsonDump = "bug.json_dump"
}
