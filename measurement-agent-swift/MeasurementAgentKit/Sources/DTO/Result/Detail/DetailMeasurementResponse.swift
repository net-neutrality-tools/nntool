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

/// This DTO contains a list of detail groups.
public class DetailMeasurementResponse: Codable {

    /// A list of detail groups.
    public var groups: [DetailMeasurementGroup]?

    /// The share text for this specific measurement.
    public var shareMeasurementText: String?

    ///
    enum CodingKeys: String, CodingKey {
        case groups
        case shareMeasurementText = "share_measurement_text"
    }
}
