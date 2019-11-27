import { GeoLocation } from '../api/request-info.api';
import { CellLocationAPI } from './cell-location.api';
import { MeasurementResultNetworkPointInTimeAPI } from './measurement-result-network-point-in-time.api';
import { PointInTimeValueAPI } from './point-in-time-value.api';
import { SignalAPI } from './signal.api';

export class TimeBasedResultAPI {
  /**
   * Start date and time for this measurement. Date and time is always stored as UTC.
   */
  public start_time: string; // TODO: change back to Date

  /**
   * End date and time for this measurement. Date and time is always stored as UTC.
   */
  public end_time: string; // TODO: change back to Date

  /**
   * Overall duration of this measurement.
   */
  public duration_ns: number;

  // GeoLocationInfo

  /**
   * List of all captured geographic locations.
   */
  public geo_locations: GeoLocation[];

  // AgentInfo

  // -> everything already submitted by ApiRequestInfo

  // DeviceInfo

  // -> everything already submitted by ApiRequestInfo

  // _ OperatingSystemInfo

  /**
   * CPU usage during the test, if available.
   */
  public cpu_usage: Array<PointInTimeValueAPI<number>>;

  /**
   * Memory usage during the test, if available.
   */
  public mem_usage: Array<PointInTimeValueAPI<number>>;

  // NetworkInfo

  // _ EmbeddedNetworkType

  /**
   * Contains all relevant network information of a single point in time.
   * @see NetworkPointInTime
   */
  public network_points_in_time: MeasurementResultNetworkPointInTimeAPI[];

  // _ CellLocationInfo

  /**
   * List of captured cell information.
   */
  public cell_locations: CellLocationAPI[];

  // _ SignalInfo

  /**
   * List of captured signal information.
   */
  public signals: SignalAPI[];
}
