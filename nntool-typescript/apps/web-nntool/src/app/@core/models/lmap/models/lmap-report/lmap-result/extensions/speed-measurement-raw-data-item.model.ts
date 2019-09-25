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
