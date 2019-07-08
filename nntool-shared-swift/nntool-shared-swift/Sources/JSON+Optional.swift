// MeasurementAgentKit: JsonHelper.swift, created on 12.05.19
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
import CodableJSON

public extension JSON {
    
    init(_ value: Bool?) {
        if let v = value {
            self = .bool(v)
        } else {
            self = .null
        }
    }
    
    init(_ value: Int?) {
        if let v = value {
            self = .int(v)
        } else {
            self = .null
        }
    }
    
    init<T>(_ value: T?) where T: BinaryInteger {
        if let v = value {
            self = .int(.init(v))
        } else {
            self = .null
        }
    }
    
    init(_ value: Double?) {
        if let v = value {
            self = .double(v)
        } else {
            self = .null
        }
    }
    
    init<T>(_ value: T?) where T: BinaryFloatingPoint {
        if let v = value {
            self = .double(.init(v))
        } else {
            self = .null
        }
    }
    
    init(_ value: String?) {
        if let v = value {
            self = .string(v)
        } else {
            self = .null
        }
    }
    
    init<T>(_ value: T?) where T: StringProtocol {
        if let v = value {
            self = .string(.init(v))
        } else {
            self = .null
        }
    }
    
    init(_ value: [JSON]?) {
        if let v = value {
            self = .array(v)
        } else {
            self = .null
        }
    }
    
    init<S>(_ value: S?) where S: Sequence, S.Element == JSON {
        if let v = value {
            self = .array(.init(v))
        } else {
            self = .null
        }
    }
    
    init(_ value: [String: JSON]?) {
        if let v = value {
            self = .object(v)
        } else {
            self = .null
        }
    }
}

public extension JSON {
    
    var uintValue: UInt? {
        guard let intVal = intValue else {
            return nil
        }
        
        return UInt(intVal)
    }
    
    var uint64Value: UInt64? {
        guard let intVal = intValue else {
            return nil
        }
        
        return UInt64(intVal)
    }
    
    var uint16Value: UInt16? {
        guard let intVal = intValue else {
            return nil
        }
        
        return UInt16(intVal)
    }
}
