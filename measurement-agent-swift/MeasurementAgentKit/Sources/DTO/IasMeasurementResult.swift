// MeasurementAgentKit: IasMeasurementResult.swift, created on 15.05.19
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

/// This DTO contains the speed measurement results from the measurement agent.
public class IasMeasurementResult: SubMeasurementResult {

    let deserializeType = "speed_result"

    /// Bytes received during the speed measurement (Download).
    public var bytesDownload: UInt64?

    /// Bytes received during the speed measurement (Download) with slow-start phase.
    public var bytesDownloadIncludingSlowStart: UInt64?

    /// Bytes transferred during the speed measurement (Upload).
    public var bytesUpload: UInt64?

    /// Bytes transferred during the speed measurement (Upload) with slow-start phase.
    public var bytesUploadIncludingSlowStart: UInt64?

    /// Duration of the RTT measurement.
    public var durationRttNs: UInt64?

    /// Duration of the download measurement.
    public var durationDownloadNs: UInt64?

    /// Duration of the upload measurement.
    public var durationUploadNs: UInt64?

    /// Relative start time of the RTT measurement in nanoseconds.
    public var relativeStartTimeRttNs: UInt64?

    /// Relative start time of the download measurement in nanoseconds.
    public var relativeStartTimeDownloadNs: UInt64?

    /// Relative start time of the upload measurement in nanoseconds.
    public var relativeStartTimeUploadNs: UInt64?

    /// Contains round trip time information measured during the measurement on the measurement agent.
    public var rttInfo: RttInfoDto?

    /// Contains a list of all captured byte transfers during the download speed measurement on the measurement agent.
    //public var downloadRawData: [SpeedMeasurementRawDataItemDto]?

    /// Contains a list of all captured byte transfers during the upload speed measurement on the measurement agent.
    //public var uploadRawData: [SpeedMeasurementRawDataItemDto]?

    /// Contains information about the connection(s) used for the speed measurement.
    public var connectionInfo: ConnectionInfoDto?

    public override init() {
        super.init()
    }

    required init(from decoder: Decoder) throws {
        fatalError("init(from:) has not been implemented")
    }

    public override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)

        var container = encoder.container(keyedBy: CodingKeys2.self)

        try container.encode(bytesDownload, forKey: .bytesDownload)
        try container.encode(bytesDownloadIncludingSlowStart, forKey: .bytesDownloadIncludingSlowStart)
        try container.encode(bytesUpload, forKey: .bytesUpload)
        try container.encode(bytesUploadIncludingSlowStart, forKey: .bytesUploadIncludingSlowStart)
        try container.encode(durationRttNs, forKey: .durationRttNs)
        try container.encode(durationDownloadNs, forKey: .durationDownloadNs)
        try container.encode(durationUploadNs, forKey: .durationUploadNs)
        try container.encode(relativeStartTimeRttNs, forKey: .relativeStartTimeRttNs)
        try container.encode(relativeStartTimeDownloadNs, forKey: .relativeStartTimeDownloadNs)
        try container.encode(relativeStartTimeUploadNs, forKey: .relativeStartTimeUploadNs)
        try container.encode(rttInfo, forKey: .rttInfo)
        //try container.encode(downloadRawData, forKey: .downloadRawData)
        //try container.encode(uploadRawData, forKey: .uploadRawData)
        try container.encode(connectionInfo, forKey: .connectionInfo)

        try container.encode(deserializeType, forKey: .deserializeType)
    }

    ///
    enum CodingKeys2: String, CodingKey {
        case bytesDownload = "bytes_download"
        case bytesDownloadIncludingSlowStart = "bytes_download_including_slow_start"
        case bytesUpload = "bytes_upload"
        case bytesUploadIncludingSlowStart = "bytes_upload_including_slow_start"
        case durationRttNs = "duration_rtt_ns"
        case durationDownloadNs = "duration_download_ns"
        case durationUploadNs = "duration_upload_ns"
        case relativeStartTimeRttNs = "relative_start_time_rtt_ns"
        case relativeStartTimeDownloadNs = "relative_start_time_download_ns"
        case relativeStartTimeUploadNs = "relative_start_time_upload_ns"
        case rttInfo = "rtt_info"
        //case downloadRawData = "download_raw_data"
        //case uploadRawData = "upload_raw_data"
        case connectionInfo = "connection_info"

        case deserializeType = "deserialize_type"
    }
}
