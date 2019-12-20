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

import { GeoLocation, MeasurementAgentType } from '../api/request-info.api';

export class BriefSubMeasurement {
  public start_test: string; // TODO: change back to Date

  public duration_ns: number;
}

export class BriefDeviceInfo {
  /**
   * Device code name.
   */
  public device_code_name: string;

  /**
   * The device name that is commonly known to users (e.g. Google Pixel).
   */
  public device_full_name: string;

  /**
   * Device operating system name.
   */
  public os_name: string;

  /**
   * Device operating system version.
   */
  public os_version: string;

  /**
   * Average CPU usage during the measurement.
   */
  public avg_cpu_usage: number;

  /**
   * Average Memory usage during the measurement.
   */
  public avg_mem_usage: number;
}

export class BriefMeasurementResponseAPI {
  // Measurement

  /**
   * The UUIDv4 identifier of the measurement object.
   */
  public uuid: string;

  /**
   * Overall start time in UTC.
   */
  public start_time: string; // TODO: change back to Date

  /**
   * Overall duration of all sub measurements.
   */
  public duration_ns: number;

  // GeoLocationInfo

  /**
   * The first accurate GeoLocation i.e. the location where the measurement was started.
   */
  public first_accurate_geo_location: GeoLocation;

  // AgentInfo

  /**
   * @see AgentType
   */
  public type: MeasurementAgentType;

  // DeviceInfo

  /**
   * @see BriefDeviceInfo
   */
  public device_info: BriefDeviceInfo;

  // NetworkInfo

  /**
   * Network type id (@see NetworkType).
   */
  public network_type_id: number;

  /**
   * Network type name (@see NetworkType).
   */
  public network_type_name: string;

  // SubMeasurement

  /**
   * Map that contains available information for each measurement type (Speed, QoS).
   * If map misses speed then no speed measurement was done, likewise for QoS, ...
   */
  public measurements: { SPEED: BriefSubMeasurement; QOS: BriefSubMeasurement };
}
