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

import { BasicResponseAPI } from '../basic-response.api';

export class Urls {
  /**
   * Base URL of the controller service of the form "[protocol]://[domain]:[port]/[path]".
   * This domain name should have A and AAAA records.
   */
  public controller_service: string;

  /**
   * IPv4-only base URL of the controller service.
   * This domain name must only have an A record.
   */
  public controller_service_ipv4: string;

  /**
   * IPv6-only base URL of the controller service.
   * This domain name must only have an AAAA record.
   */
  public controller_service_ipv6: string;

  /**
   * Base URL of the collector service of the form "[protocol]://[domain]:[port]/[path]".
   * This domain name should have A and AAAA records.
   */
  public collector_service: string;

  /**
   * Base URL of the map service of the form "[protocol]://[domain]:[port]/[path]".
   */
  public map_service: string;

  /**
   * Base URL of the statistic service of the form "[protocol]://[domain]:[port]/[path]".
   */
  public statistic_service: string;

  /**
   * Base URL of the web site of the form "[protocol]://[domain]:[port]/[path]".
   */
  public website: string;
}

export enum QoSMeasurementType {
  TCP = 'TCP',
  UDP = 'UDP',
  DNS = 'DNS',
  NON_TRANSPARENT_PROXY = 'NON_TRANSPARENT_PROXY',
  HTTP_PROXY = 'HTTP_PROXY',
  VOIP = 'VOIP',
  TRACEROUTE = 'TRACEROUTE',
  WEBSITE = 'WEBSITE',
  SIP = 'SIP',
  ECHO_PROTOCOL = 'ECHO_PROTOCOL',

  MKIT_DASH = 'MKIT_DASH',
  MKIT_WEB_CONNECTIVITY = 'MKIT_WEB_CONNECTIVITY'
}

export class TranslatedQoSTypeInfo {
  /**
   * The translated QoS type name.
   */
  public name: string;

  /**
   * The translated QoS type description.
   */
  public description: string;
}

export class VersionResponse {
  /**
   * Controller service version number.
   */
  public controller_service_version: string;

  /**
   * Collector service version number.
   */
  public collector_service_version: string;

  /**
   * Result service version number.
   */
  public result_service_version: string;

  /**
   * Map service version number.
   */
  public map_service_version: string;

  /**
   * Statistic service version number.
   */
  public statistic_service_version: string;
}

export interface SettingsResponseAPI extends BasicResponseAPI {
  /**
   * @see Urls
   */
  urls: Urls;

  /**
   * Map of QoS measurement types to translated type information.
   */
  qos_type_info: Map<QoSMeasurementType, TranslatedQoSTypeInfo>;

  /**
   * @see VersionResponse
   */
  version: VersionResponse;
}
