// MeasurementAgentKit: DetailMeasurementGroup.swift, created on 24.07.19
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

/// Measurement detail group object which contains a translated title, an optional description, and icon and the items.
public class DetailMeasurementGroup: Codable {

    /// The already translated title of the given group.
    public var title: String?

    /// The already translated (optional) description of the given group.
    public var description: String?

    /// The icon to be used for the given group (as a single char in the corresponding icon font).
    public var iconCharacter: String?

    /// Contains all the entries of the given group.
    public var items: [DetailMeasurementGroupItem]?

    public init() {

    }

    ///
    enum CodingKeys: String, CodingKey {
        case title
        case description
        case iconCharacter = "icon_character"
        case items
    }
}
