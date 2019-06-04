//
//  JSON+Optional.swift
//  QoSKit_iOS
//
//  Created by Benjamin Pucher on 12.05.19.
//

import Foundation
import CodableJSON

extension JSON {

    public init(_ value: Bool?) {
        if let v = value {
            self = .bool(v)
        } else {
            self = .null
        }
    }

    public init(_ value: Int?) {
        if let v = value {
            self = .int(v)
        } else {
            self = .null
        }
    }

    public init<T>(_ value: T?) where T: BinaryInteger {
        if let v = value {
            self = .int(.init(v))
        } else {
            self = .null
        }
    }

    public init(_ value: Double?) {
        if let v = value {
            self = .double(v)
        } else {
            self = .null
        }
    }

    public init<T>(_ value: T?) where T: BinaryFloatingPoint {
        if let v = value {
            self = .double(.init(v))
        } else {
            self = .null
        }
    }

    public init(_ value: String?) {
        if let v = value {
            self = .string(v)
        } else {
            self = .null
        }
    }

    public init<T>(_ value: T?) where T: StringProtocol {
        if let v = value {
            self = .string(.init(v))
        } else {
            self = .null
        }
    }

    public init(_ value: [JSON]?) {
        if let v = value {
            self = .array(v)
        } else {
            self = .null
        }
    }

    public init<S>(_ value: S?) where S: Sequence, S.Element == JSON {
        if let v = value {
            self = .array(.init(v))
        } else {
            self = .null
        }
    }

    public init(_ value: [String: JSON]?) {
        if let v = value {
            self = .object(v)
        } else {
            self = .null
        }
    }
}

extension JSON {

    public var uintValue: UInt? {
        guard let intVal = intValue else {
            return nil
        }

        return UInt(intVal)
    }

    public var uint64Value: UInt64? {
        guard let intVal = intValue else {
            return nil
        }

        return UInt64(intVal)
    }

    public var uint16Value: UInt16? {
        guard let intVal = intValue else {
            return nil
        }

        return UInt16(intVal)
    }
}
