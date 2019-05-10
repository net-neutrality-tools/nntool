// MeasurementAgentKit: JsonHelper.swift, created on 02.04.19
/*******************************************************************************
 * Copyright 2019 bp (alladin-IT GmbH)
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

///
class JsonHelper {

    ///
    class func getPreconfiguredJSONDecoder() -> JSONDecoder {
        let decoder = JSONDecoder()

        // Unfortunately, .iso8601 doesn't support every format from RFC 3339
        //decoder.dateDecodingStrategy = .iso8601

        decoder.dateDecodingStrategy = .formatted(getIso8601DateFormatter())

        return decoder
    }

    ///
    class func getPreconfiguredJSONEncoder() -> JSONEncoder {
        let encoder = JSONEncoder()

        // Unfortunately, .iso8601 doesn't support every format from RFC 3339
        //encoder.dateEncodingStrategy = .iso8601

        encoder.dateEncodingStrategy = .formatted(getIso8601DateFormatter())

        encoder.outputFormatting = .prettyPrinted

        return encoder
    }

    private class func getIso8601DateFormatter() -> DateFormatter {
        let dateFormatter = DateFormatter()
        //dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"

        // TODO: maybe use approach from https://stackoverflow.com/a/46538676

        return dateFormatter
    }
}

///////////////////////////

public indirect enum ObjectiveValue: Decodable {
    case int(Int)
    case double(Double)
    case string(String)
    case bool(Bool)
    case `nil`

    public init(from decoder: Decoder) throws {
        let singleValueContainer = try decoder.singleValueContainer()

        if let value = try? singleValueContainer.decode(Bool.self) {
            self = .bool(value)
            return
        }

        if let value = try? singleValueContainer.decode(Int.self) {
            self = .int(value)
            return
        }

        if let value = try? singleValueContainer.decode(String.self) {
            self = .string(value)
            return
        }

        if let value = try? singleValueContainer.decode(Double.self) {
            self = .double(value)
            return
        }

        if singleValueContainer.decodeNil() {
            self = .nil
            return
        }

        throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Could not find reasonable type to map to ObjectiveValue"))
    }
}

extension ObjectiveValue {

    public var int: Int? {
        switch self {
        case .int(let value):
            return value
        default:
            return nil
        }
    }

    public var string: String? {
        switch self {
        case .string(let value):
            return value
        default:
            return nil
        }
    }

    public var double: Double? {
        switch self {
        case .double(let value):
            return value
        default:
            return nil
        }
    }

    public var bool: Bool? {
        switch self {
        case .bool(let value):
            return value
        default:
            return nil
        }
    }

    public var isNil: Bool {
        switch self {
        case .nil:
            return true
        default:
            return false
        }
    }
}
