/***************************************************************************
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
***************************************************************************/

import Foundation

struct RTPHeaderFlags {

    var version: UInt8
    var hasPadding: Bool
    var hasExtension: Bool
    var csrcCount: UInt8

    var hasMarker: Bool
    var payloadType: UInt8
}

extension RTPHeaderFlags: RawRepresentable {
    typealias RawValue = Data

    init?(rawValue: Self.RawValue) {
        guard rawValue.count == MemoryLayout<UInt16>.size else {
            return nil
        }

        let flags = ByteUtil.convertData(rawValue, to: UInt16.self)

        self.init(
            version: UInt8(flags >> 14),
            hasPadding: ((flags >> 13) & 1) == 1,
            hasExtension: ((flags >> 12) & 1) == 1,
            csrcCount: UInt8((flags >> 8) & 0xF),
            hasMarker: ((flags >> 7) & 1) == 1,
            payloadType: UInt8(flags & 0x7F)
        )
    }

    var rawValue: Self.RawValue {
        var data = Data()

        var flags: UInt16 = 0

        flags |= UInt16(version) << 14

        if hasPadding {
            flags |= 1 << 13
        }

        if hasExtension {
            flags |= 1 << 12
        }

        flags |= (UInt16(csrcCount) & 0xF) << 8

        if hasMarker {
            flags |= 1 << 7
        }

        flags |= UInt16(payloadType & 0x7F)

        var flagsBigEndian = flags.bigEndian
        data.append(UnsafeBufferPointer(start: &flagsBigEndian, count: 1))

        return data
    }
}

struct RTPHeader {

    static let minLength = 12
    static let csrcByteLength = MemoryLayout<UInt32>.size

    var flags: RTPHeaderFlags
    var sequenceNumber: UInt16
    var timestamp: UInt32
    var ssrcIdentifier: UInt32
    var csrcIdentifiers = [UInt32]() {
        didSet {
            flags.csrcCount = UInt8(truncatingIfNeeded: csrcIdentifiers.count)
        }
    }

    var headerExtension: Data? {
        didSet {
            flags.hasExtension = headerExtension != nil
        }
    }

    var byteLength: Int {
        return RTPHeader.minLength + csrcIdentifiers.count * RTPHeader.csrcByteLength + (headerExtension?.count ?? 0)
    }
}

extension RTPHeader: RawRepresentable {
    typealias RawValue = Data

    init?(rawValue: Self.RawValue) {
        guard rawValue.count >= RTPHeader.minLength else {
            return nil // RTP packet size too small
        }

        guard let flags = RTPHeaderFlags(rawValue: rawValue[0..<2]) else {
            return nil // Could not deserialize flags
        }

        let sequenceNumber = ByteUtil.convertData(rawValue[2..<4], to: UInt16.self)
        let timestamp = ByteUtil.convertData(rawValue[4..<8], to: UInt32.self)
        let ssrcIdentifier = ByteUtil.convertData(rawValue[8..<12], to: UInt32.self)

        var csrcIdentifiers = [UInt32]()

        for i in 0..<flags.csrcCount {
            let csrcOffset = RTPHeader.minLength + Int(i) * RTPHeader.csrcByteLength
            csrcIdentifiers.append(ByteUtil.convertData(rawValue[csrcOffset..<csrcOffset + RTPHeader.csrcByteLength], to: UInt32.self))
        }

        var headerLength = RTPHeader.minLength + Int(flags.csrcCount) * RTPHeader.csrcByteLength
        var headerExtension: Data?

        // check extension
        if flags.hasExtension {
            headerExtension = rawValue[headerLength..<headerLength + 4]
            headerLength += 4
        }

        // TODO: padding

        self.init(
            flags: flags, sequenceNumber: sequenceNumber,
            timestamp: timestamp,
            ssrcIdentifier: ssrcIdentifier,
            csrcIdentifiers: csrcIdentifiers,
            headerExtension: headerExtension
        )
    }

    var rawValue: Self.RawValue {
        var data = Data()

        data.append(flags.rawValue)

        var sequenceNumberBigEndian = sequenceNumber.bigEndian
        data.append(UnsafeBufferPointer(start: &sequenceNumberBigEndian, count: 1))

        var timestampBigEndian = timestamp.bigEndian
        data.append(UnsafeBufferPointer(start: &timestampBigEndian, count: 1))

        var ssrcIdentifierBigEndian = ssrcIdentifier.bigEndian
        data.append(UnsafeBufferPointer(start: &ssrcIdentifierBigEndian, count: 1))

        csrcIdentifiers.forEach {
            var csrcBigEndian = $0.bigEndian
            data.append(UnsafeBufferPointer(start: &csrcBigEndian, count: 1))
        }

        return data
    }
}

struct RTPPacket {
    var header: RTPHeader
    var payload: Data

    var byteLength: Int {
        return header.byteLength + payload.count
    }
}

extension RTPPacket: RawRepresentable {
    typealias RawValue = Data

    init?(rawValue: Self.RawValue) {
        guard let header = RTPHeader(rawValue: rawValue) else {
            return nil // Could not deserialize header
        }

        let payload = rawValue[header.byteLength...]

        self.init(header: header, payload: payload)
    }

    var rawValue: Self.RawValue {
        var data = Data()

        data.append(header.rawValue)
        data.append(payload)

        return data
    }
}
