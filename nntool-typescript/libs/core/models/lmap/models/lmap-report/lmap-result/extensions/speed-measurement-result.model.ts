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

import { ConnectionInfo } from './connection-info.model';
import { RttInfo } from './rtt-info.model';
import { SpeedMeasurementRawDataItem } from './speed-measurement-raw-data-item.model';
import { SubMeasurementResult } from './sub-measurement-result.model';

export class SpeedMeasurementResult extends SubMeasurementResult {
  /**
   * Bytes received during the speed measurement (Download).
   */
  public bytes_download: number;

  /**
   * Bytes received during the speed measurement (Download) with slow-start phase.
   */
  public bytes_download_including_slow_start: number;

  /**
   * Bytes transferred during the speed measurement (Upload).
   */
  public bytes_upload: number;

  /**
   * Bytes transferred during the speed measurement (Upload) with slow-start phase.
   */
  public bytes_upload_including_slow_start: number;

  /**
   * Duration of the RTT measurement.
   */
  public duration_rtt_ns: number;

  /**
   * Duration of the download measurement.
   */
  public duration_download_ns: number;

  /**
   * Duration of the upload measurement.
   */
  public duration_upload_ns: number;

  /**
   * Relative start time of the RTT measurement in nanoseconds.
   */
  public relative_start_time_rtt_ns: number;

  /**
   * Relative start time of the download measurement in nanoseconds.
   */
  public relative_start_time_download_ns: number;

  /**
   * Relative start time of the upload measurement in nanoseconds.
   */
  public relative_start_time_upload_ns: number;

  // RttInfo

  /**
   * @see RttInfoDto
   */
  public rtt_info: RttInfo;

  // SpeedMeasurementRawData

  /**
   * Contains a list of all captured byte transfers during the download speed measurement on the measurement agent.
   */
  public download_raw_data: SpeedMeasurementRawDataItem[];

  /**
   * Contains a list of all captured byte transfers during the upload speed measurement on the measurement agent.
   */
  public uploadRawData: SpeedMeasurementRawDataItem[];

  // ConnectionInfo

  /**
   * @see ConnectionInfoDto
   */
  public connection_info: ConnectionInfo;
}
