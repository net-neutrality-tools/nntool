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

export class SpeedMeasurementRawDataItem {
  /**
   * The stream id (numeric value starting from 0).
   */
  public stream_id: number;

  /**
   * Relative time since start of the speed measurement.
   */
  public relative_time_ns: number;

  /**
   * Bytes transmitted or received since start of the speed measurement.
   */
  public bytes: number;
}
