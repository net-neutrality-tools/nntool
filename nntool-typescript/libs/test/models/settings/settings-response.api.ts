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
