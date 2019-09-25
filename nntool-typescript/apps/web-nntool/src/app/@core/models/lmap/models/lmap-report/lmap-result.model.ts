import { LmapOption } from '../shared/lmap-option.model';
import { QoSMeasurementResult } from './lmap-result/extensions/qos-measurement-result.model';
import { SpeedMeasurementResult } from './lmap-result/extensions/speed-measurement-result.model';
import { LmapConflict } from './lmap-result/lmap-conflict.model';

export class LmapResult {
  /**
   * The name of the Schedule that produced the result.
   */
  public schedule?: string;

  /**
   * The name of the Action in the Schedule that produced the result.
   */
  public action?: string;

  /**
   * The name of the Task that produced the result.
   */
  public task?: string;

  /**
   * This container is a placeholder for runtime parameters defined in Task-specific data models
   * augmenting the base LMAP report data model.
   */
  public parameters: any;

  /**
   * The list of options there were in use when the measurement was performed.
   * This list must include both the Task-specific options as well as the Action-specific options.
   */
  public option: LmapOption[];

  /**
   * A tag contains additional information that is passed with the result record to the Collector.
   * This is the joined set of tags defined for the Task object, the Schedule object, and the Action object.
   * A tag can be used to carry the Measurement Cycle ID.
   */
  public tag: string[];

  /**
   * The date and time of the event that triggered the Schedule of the Action
   * that produced the reported result values.
   * The date and time does not include any added randomization.
   */
  public event?: string; // TODO: change back to Date

  /**
   * The date and time when the Task producing this result started.
   */
  public start: string; // TODO: change back to Date

  /**
   * The date and time when the Task producing this result finished.
   */
  public end?: string; // TODO: change back to Date

  /**
   * The optional cycle number is the time closest to the time reported in the event leaf
   * that is a multiple of the cycle-interval of the event that triggered the execution of the Schedule.
   * The value is only present if the event that triggered the execution of the Schedule has a defined cycle-interval.
   */
  public 'cycle-number'?: string;

  /**
   * The status code returned by the execution of this Action.
   *
   * A status code returned by the execution of a Task.  Note
   * that the actual range is implementation dependent, but it
   * should be portable to use values in the range 0..127 for
   * regular exit codes.  By convention, 0 indicates successful
   * termination.  Negative values may be used to indicate
   * abnormal termination due to a signal; the absolute value
   * may identify the signal number in this case.
   */
  public status: number;

  /**
   * The names of Tasks overlapping with the execution of the Task that has produced this result.
   */
  public conflict: LmapConflict[];

  /**
   * A list of results. Replaces the table list from LMAP
   */
  public results: Array<SpeedMeasurementResult | QoSMeasurementResult>;
}
