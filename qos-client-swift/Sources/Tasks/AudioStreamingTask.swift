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
import CodableJSON
import Repeat
import nntool_shared_swift
import qos_client_cpp

struct AudioStreamingResult: Decodable {
    var audioStartTimeNs: UInt64?
    var stallsNs: [UInt64]?
    
    ///
    enum CodingKeys: String, CodingKey {
        case audioStartTimeNs = "audio_start_time_ns"
        case stallsNs = "stalls_ns"
    }
}

class AudioStreamingTask: QoSTask {

    private var url: String
    private var bufferDurationNs: UInt64
    private var playbackDuratioNs: UInt64

    private var audioStreamingResult: AudioStreamingResult?
    
    private var progressTimer: Repeater?
    
    ///
    enum CodingKeys4: String, CodingKey {
        case url = "url"
        case bufferDurationNs = "buffer_duration_ns"
        case playbackDuratioNs = "target_playback_duration_ns"
    }

    override var statusKey: String? {
        return "audio_streaming_status"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["audio_streaming_objective_target_url"] = JSON(url)
        r["audio_streaming_objective_buffer_duration_ns"] = JSON(bufferDurationNs)
        r["audio_streaming_objective_playback_duration_ns"] = JSON(playbackDuratioNs)

        r["audio_start_time_ns"] = JSON(audioStreamingResult?.audioStartTimeNs)
        r["stalls_ns"] = JSON(audioStreamingResult?.stallsNs?.map() { JSON($0) })

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        url = try container.decodeWithStringFallback(String.self, forKey: .url)
        bufferDurationNs = try container.decodeWithStringFallback(UInt64.self, forKey: .bufferDurationNs)
        playbackDuratioNs = try container.decodeWithStringFallback(UInt64.self, forKey: .playbackDuratioNs)
        
        try super.init(from: decoder)
    }

    override func taskMain() {
        guard let wrapper = AudioStreamingWrapper(
            url: url,
            bufferDurationNs: bufferDurationNs,
            playbackDurationNs: playbackDuratioNs
        ) else {
            status = .error
            return
        }
        
        let semaphore = DispatchSemaphore(value: 0)
        var resultString: String?
        
        DispatchQueue.global(qos: .background).async {
            self.progressTimer = Repeater.every(.milliseconds(100)) { _ in
                self.progress.completedUnitCount += Int64((UInt64(self.progress.totalUnitCount) * NSEC_PER_MSEC * 100 / self.playbackDuratioNs))
            }
            self.progressTimer?.start()
            
            resultString = wrapper.start()
            
            self.progressTimer?.removeAllObservers(thenStop: true)
            self.progressTimer = nil
            
            semaphore.signal()
        }
        
        let semaphoneTimeout = DispatchTime.now() + DispatchTimeInterval.nanoseconds(Int(truncatingIfNeeded: timeoutNs))
        if semaphore.wait(timeout: semaphoneTimeout) == .timedOut {
            //wrapper.stop()
            status = .timeout
            return
        }

        guard
            let resultData = resultString?.data(using: .utf8),
            let audioStreamingResult = try? JSONDecoder().decode(AudioStreamingResult.self, from: resultData)
        else {
            status = .error
            return
        }
        
        if status == .unknown {
            status = .ok
        }
    }
}
