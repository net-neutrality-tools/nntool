import { Reason } from './reason.enum';
import { Status } from './status.enum';

export abstract class SubMeasurementResult {
  /**
   * Start time in nanoseconds relative to the start time of the overall measurement object.
   */
  public relative_start_time_ns: number;

  /**
   * End time in nanoseconds relative to the end time of the overall measurement object.
   */
  public relative_end_time_ns: number;

  // MeasurementStatusInfo

  /**
   * @see StatusDto
   */
  public status: Status;

  /**
   * @see ReasonDto
   */
  public reason: Reason;

  public deserialize_type: string;
}
