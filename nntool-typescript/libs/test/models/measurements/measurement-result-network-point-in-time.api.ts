import { GeoLocation } from '../api/request-info.api';
import { PointInTimeValueAPI } from './point-in-time-value.api';

export class MeasurementResultNetworkPointInTimeAPI {
  /**
   * Time and date the signal information was captured (UTC).
   */
  public time: string;

  /**
   * Relative time in nanoseconds (to test begin).
   */
  public relative_time_ns: number;

  /**
   * Network type id as it gets returned by the Android API.
   */
  public network_type_id: number;

  // _ ProviderInfo

  // _ NetworkWifiInfo

  /**
   * SSID of the network.
   */
  public ssid: string;

  /**
   * BSSID of the network.
   */
  public bssid: string;

  // _ NetworkMobileInfo

  /**
   * The network operator country code (e.g. "AT"), if available.
   */
  public network_country: string;

  /**
   * The MCC/MNC of the network operator, if available.
   */
  public network_operator_mcc_mnc: string;

  /**
   * The network operator name, if available.
   */
  public network_operator_name: string;

  /**
   * The SIM operator country code (e.g. "AT"), if available.
   */
  public sim_country: string;

  /**
   * The MCC/MNC of the SIM operator, if available.
   */
  public sim_operator_mcc_mnc: string;

  /**
   * SIM operator name, if available.
   */
  public sim_operator_name: string;
}
