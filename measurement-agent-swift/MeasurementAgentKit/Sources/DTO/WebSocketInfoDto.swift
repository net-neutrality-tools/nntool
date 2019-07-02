// MeasurementAgentKit: WebSocketInfoDto.swift, created on 15.05.19
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

/// This class contains additional information gathered from the WebSocket protocol.
public class WebSocketInfoDto: Codable {

    /// Size of a transmitted frame over the WebSocket protocol.
    public var frameSize: Int? // *load_frame_size

    /// Number of frames sent over the WebSocket protocol during measurement excluding slow-start phase.
    public var frameCount: Int? // *load_frames

    /// Number of frames sent over the WebSocket protocol during measurement including slow-start phase.
    public var frameCountIncludingSlowStart: Int? // *load_frames_total

    /// The overhead sent during the communication via the WebSocket protocol excluding slow-start phase.
    public var overhead: Int? // *load_overhead

    /// The overhead sent during the communication via the WebSocket protocol including slow-start phase.
    public var overheadIncludingSlowStart: Int? // *load_overhead_total

    /// The protocol overhead of a single WebSocket frame.
    public var overheadPerFrame: Int? // *load_overhead_per_frame

    ///
    enum CodingKeys: String, CodingKey {
        case frameSize = "frame_size"
        case frameCount = "frame_count"
        case frameCountIncludingSlowStart = "frame_count_including_slow_start"
        case overhead
        case overheadIncludingSlowStart = "overhead_including_slow_start"
        case overheadPerFrame = "overhead_per_frame"
    }
}
