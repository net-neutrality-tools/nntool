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

/// DTO that wraps server errors and/or exceptions.
/// The Java stack-trace is only added if the service runs in development mode.
class ApiError: Codable {

    /// Date and time at which the error occurred.
    var time: Date?

    /// URI path/resource that caused the error.
    var path: String?

    /// Status code for the error. Example: 400, 404, 500, ...
    var status: Int?

    /// String representation of the status. Example: "Internal Server Error, "Not Found", ...
    var error: String?

    /// The error or exception message. Example: "java.lang.RuntimeException".
    var message: String?

    /// Exception class name.
    var exception: String?

    /// Exception stack trace.
    var trace: String?
}
