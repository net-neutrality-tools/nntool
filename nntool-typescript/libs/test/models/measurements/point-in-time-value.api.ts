import { GeoLocation } from '../api/request-info.api';

export class PointInTimeValueAPI<T> {
  /**
   * The relative time in nanoseconds to the test start.
   */
  public relative_time_ns: number;

  /**
   * The value recorded at this point in time.
   */
  public value: T;

  constructor(value: T, relative_time_ns: number) {
    this.value = value;
    this.relative_time_ns = relative_time_ns;
  }
}
