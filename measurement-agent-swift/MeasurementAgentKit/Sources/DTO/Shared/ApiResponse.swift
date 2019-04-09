// MeasurementAgentKit: ApiResponse.swift, created on 29.03.19
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

// Decoding of errors does not work if this extends from ApiBase.
// Because of that the data property from ApiBase was moved into here.

/// Object that is used as wrapper for every response.
class ApiResponse<T: Codable>: Codable /*: ApiBase<T>*/ {

    /// Actual data that is returned for the request/response.
    var data: T?

    /// Optional list of errors that occurred during request processing.
    var errors: [ApiError]?
}
