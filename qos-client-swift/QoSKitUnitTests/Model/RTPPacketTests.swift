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

import XCTest
@testable import QoSKit

class RTPPacketTests: XCTestCase {

    func testRTPPacket() {
        let rtpPacket = RTPPacket(
            header: RTPHeader(
                flags: RTPHeaderFlags(
                    version: 2, hasPadding: false, hasExtension: false, csrcCount: 2, hasMarker: false, payloadType: 123
                ),
                sequenceNumber: 13, timestamp: 15, ssrcIdentifier: 16, csrcIdentifiers: [100, 200], headerExtension: Data()
            ),
            payload: Data()
        )

        XCTAssert(rtpPacket == RTPPacket(rawValue: rtpPacket.rawValue)!)
    }

    func testFlags() {
        var flags: RTPHeaderFlags

        flags = RTPHeaderFlags(version: 0, hasPadding: false, hasExtension: false, csrcCount: 0, hasMarker: false, payloadType: 0)

        XCTAssert(flags == RTPHeaderFlags(rawValue: flags.rawValue)!)

        flags = RTPHeaderFlags(version: 2, hasPadding: true, hasExtension: true, csrcCount: 0, hasMarker: true, payloadType: 0)

        XCTAssert(flags == RTPHeaderFlags(rawValue: flags.rawValue)!)

        flags = RTPHeaderFlags(version: 2, hasPadding: false, hasExtension: true, csrcCount: 2, hasMarker: true, payloadType: 123)

        XCTAssert(flags == RTPHeaderFlags(rawValue: flags.rawValue)!)

        flags = RTPHeaderFlags(version: 2, hasPadding: false, hasExtension: true, csrcCount: 5, hasMarker: false, payloadType: 12)

        XCTAssert(flags == RTPHeaderFlags(rawValue: flags.rawValue)!)
    }
}
