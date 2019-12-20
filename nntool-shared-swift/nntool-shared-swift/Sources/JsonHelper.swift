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

///
public class JsonHelper {
    
    ///
    public class func getPreconfiguredJSONDecoder() -> JSONDecoder {
        let decoder = JSONDecoder()
        
        // Unfortunately, .iso8601 doesn't support every format from RFC 3339
        //decoder.dateDecodingStrategy = .iso8601
        
        decoder.dateDecodingStrategy = .formatted(getIso8601DateFormatter())
        
        return decoder
    }
    
    ///
    public class func getPreconfiguredJSONEncoder() -> JSONEncoder {
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
    
    public class func debugPrintObject<T: Encodable>(_ obj: T) -> String {
        do {
            let data = try JSONEncoder().encode(obj)
            return String(data: data, encoding: .utf8) ?? "-encoding failed-"
        } catch {
            return "-encoding failed-"
        }
    }
}

public extension KeyedDecodingContainer {
 
    enum ParseError: Error {
        case parseError(String)
    }
    
    public func decodeIfPresentWithStringFallback<T: Decodable>(_ type: T.Type, forKey key: KeyedDecodingContainer<K>.Key) -> T? where T: LosslessStringConvertible {
        
        if let val = try? decodeIfPresent(type, forKey: key) {
            return val
        } else if let str = try? decodeIfPresent(String.self, forKey: key), let val = T(str) {
            return val
        }
        
        return nil
    }
    
    public func decodeWithStringFallback<T: Decodable>(_ type: T.Type, forKey key: KeyedDecodingContainer<K>.Key) throws -> T where T: LosslessStringConvertible {
        
        if let val = decodeIfPresentWithStringFallback(type, forKey: key) {
            return val
        } else {
            throw ParseError.parseError("Could not parse \(key)")
        }
    }
}
