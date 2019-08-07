// MeasurementAgentKit: BriefIASMeasurement.swift, created on 24.07.19
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

/// Brief/short information of a speed measurement.
public class BriefIASMeasurement: BriefSubMeasurement {

    /// The calculated (average) download throughput in bits per second.
    public var throughputAvgDownloadBps: UInt64?

    /// The calculated (average) upload throughput in bits per second.
    public var throughputAvgUploadBps: UInt64?

    /// Average RTT value in nanoseconds.
    public var rttAverageNs: UInt64?

    /// Median RTT value in nanoseconds.
    public var rttMedianNs: UInt64?

    public override init() {
        super.init()
    }

    required init(from decoder: Decoder) throws {
        try super.init(from: decoder)

        let container = try decoder.container(keyedBy: CodingKeys2.self)

        try throughputAvgDownloadBps = container.decodeIfPresent(UInt64.self, forKey: .throughputAvgDownloadBps)
        try throughputAvgUploadBps = container.decodeIfPresent(UInt64.self, forKey: .throughputAvgUploadBps)
        try rttAverageNs = container.decodeIfPresent(UInt64.self, forKey: .rttAverageNs)
        try rttMedianNs = container.decodeIfPresent(UInt64.self, forKey: .rttMedianNs)
    }

    public override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)

        var container = encoder.container(keyedBy: CodingKeys2.self)

        try container.encode(throughputAvgDownloadBps, forKey: .throughputAvgDownloadBps)
        try container.encode(throughputAvgUploadBps, forKey: .throughputAvgUploadBps)
        try container.encode(rttAverageNs, forKey: .rttAverageNs)
        try container.encode(rttMedianNs, forKey: .rttMedianNs)
    }

    ///
    enum CodingKeys2: String, CodingKey {
        case throughputAvgDownloadBps = "throughput_avg_download_bps"
        case throughputAvgUploadBps = "throughput_avg_upload_bps"
        case rttAverageNs = "rtt_average_ns"
        case rttMedianNs = "rtt_median_ns"
    }
}
