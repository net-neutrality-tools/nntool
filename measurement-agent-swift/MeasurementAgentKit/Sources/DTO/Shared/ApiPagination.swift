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

/// Wrapper for paginated responses (e.g. list of measurements).
public class ApiPagination<T: Codable>: Codable {

    /// Paginated list of objects.
    public var content: [T]?

    /// Current page number (>= 0).
    public var pageNumber: Int

    /// Current page size (> 0).
    public var pageSize: Int

    /// Total amount of pages (>= 0).
    public var totalPages: Int

    /// Total amount of objects (>= 0).
    public var totalElements: Int

    //
    enum CodingKeys: String, CodingKey {
        case content
        case pageNumber    = "page_number"
        case pageSize      = "page_size"
        case totalPages    = "total_pages"
        case totalElements = "total_elements"
    }
}
