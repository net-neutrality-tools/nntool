// MeasurementAgentKit: RttInfoDto.swift, created on 15.05.19
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

/// Contains information from a single round trip time measurement on the measurement agent.
public class RttDto: Codable {

    /// Round trip time recorded in nanoseconds.
    public var rttNs: UInt64?

    /// Relative time in nanoseconds (to test begin).
    public var relativeTimeNs: UInt64?

    public init() {

    }

    public init(rttNs: UInt64?, relativeTimeNs: UInt64?) {
        self.rttNs = rttNs
        self.relativeTimeNs = relativeTimeNs
    }

    ///
    enum CodingKeys: String, CodingKey {
        case rttNs = "rtt_ns"
        case relativeTimeNs = "relative_time_ns"
    }
}

/// Contains round trip time information measured during the measurement on the measurement agent.
public class RttInfoDto: Codable {

    /// List of all measured RTTs.
    public var rtts: [RttDto]?

    /// The number of RTT packets to send, as instructed by the server.
    public var requestedNumPackets: Int?

    /// The actual number of sent RTT packets.
    public var numSent: Int?

    /// The actual number of received RTT packets.
    public var numReceived: Int?

    /// The actual number of failed RTT packets.
    public var numError: Int?

    /// The actual number of missing RTT packets.
    public var numMissing: Int?

    /// The actual size of RTT packets.
    public var packetSize: Int?

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /// Average rtt in nanoseconds
    public var averageNs: UInt64?

    /// Maximum rtt in nanoseconds
    public var maximumNs: UInt64?

    /// Median rtt in nanoseconds
    public var medianNs: UInt64?

    /// Minimum rtt in nanoseconds
    public var minimumNs: UInt64?

    /// Standard deviation rtt in nanoseconds
    public var standardDeviationNs: UInt64?

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public init() {

    }

    ///
    enum CodingKeys: String, CodingKey {
        case rtts = "rtts"
        case requestedNumPackets = "requested_num_packets"
        case numSent = "num_sent"
        case numReceived = "num_received"
        case numError = "num_error"
        case numMissing = "num_missing"
        case packetSize = "packet_size"

        case averageNs = "average_ns"
        case maximumNs = "maximum_ns"
        case medianNs = "median_ns"
        case minimumNs = "minimum_ns"
        case standardDeviationNs = "standard_deviation_ns"
    }
}
