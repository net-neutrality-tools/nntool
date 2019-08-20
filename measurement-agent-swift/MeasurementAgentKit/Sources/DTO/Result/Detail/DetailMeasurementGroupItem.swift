// MeasurementAgentKit: DetailMeasurementGroupItem.swift, created on 24.07.19
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

/// This class specifies a single detail item with key, translated title, value and the unit of the value.
public class DetailMeasurementGroupItem: Codable {

    /// The key is the path to the chosen field inside the data model, e.g. "device_info.model".
    public var key: String?

    /// The translated title of this item.
    public var title: String?

    /// The actual value of this item in the given unit.
    public var value: String?

    /// The unit of the value.
    public var unit: String?

    ///
    enum CodingKeys: String, CodingKey {
        case key
        case title
        case value
        case unit
    }
}
