// MeasurementAgentKit: LmapReportDto.swift, created on 28.03.19
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

/// This module defines a data model for reporting results from Measurement Agents,
/// which are part of a Large-Scale Measurement Platform (LMAP), to result data Collectors.
class LmapReportDto: Codable {

    /// The date and time when this result report was sent to a Collector.
    var date: Date?

    /// The agent-id of the agent from which this report originates.
    var agentId: String? // uuid

    /// The group-id of the agent from which this report originates.
    var groupId: String?

    /// The measurement-point of the agent from which this report originates.
    var measurementPoint: String?

    /// The list of Tasks for which results are reported.
    var results: [LmapResultDto]?

    /// Additional information that is sent by agent alongside the request.
    var additionalRequestInfo: ApiRequestInfo?

    /// This module defines a data model for reporting time based results from Measurement Agents.
    var timeBasedResult: TimeBasedResultDto?

    ///
    enum CodingKeys: String, CodingKey {
        case date
        case agentId = "agent-id"
        case groupId = "group-id"
        case measurementPoint = "measurement-point"
        case results
        case additionalRequestInfo = "additional_request_info-point"
        case timeBasedResult = "time_based_result-point"
    }
}
