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

export class WebSocketInfo {
  /**
   * Size of a transmitted frame over the WebSocket protocol.
   */
  public frame_size: number;

  /**
   * Number of frames sent over the WebSocket protocol during measurement excluding slow-start phase.
   */
  public frame_count: number;

  /**
   * Number of frames sent over the WebSocket protocol during measurement including slow-start phase.
   */
  public frame_count_including_slow_start: number;

  /**
   * The overhead sent during the communication via the WebSocket protocol excluding slow-start phase.
   */
  public overhead: number;

  /**
   * The overhead sent during the communication via the WebSocket protocol including slow-start phase.
   */
  public overhead_per_frame_including_slow_start: number;

  /**
   * The overhead a single frame produces on average.
   */
  public overhead_per_frame: number;
}
