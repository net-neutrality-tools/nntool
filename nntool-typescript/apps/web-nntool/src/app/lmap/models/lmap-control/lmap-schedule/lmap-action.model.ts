import { LmapOption } from '../../shared/lmap-option.model';
import { LmapState } from '../../shared/lmap-state.enum';

export class LmapAction {
  /**
   * The unique identifier for this Action.
   */
  public name: string;

  /**
   * The Task invoked by this Action.
   */
  public task: string;

  /**
   * This container is a placeholder for runtime parameters
   * defined in Task-specific data models augmenting the base LMAP report data model.
   */
  public parameters: any;

  /**
   * The list of Action-specific options that are appended to the list of Task-specific options.
   */
  public option: LmapOption[];

  /**
   * A set of Schedules receiving the output produced by this Action.
   * The output is stored temporarily since the Destination Schedules will in general not be running when output is passed to them.
   * The behavior of an Action passing data to its own Schedule is implementation specific.
   */
  public destination: string[];

  /**
   * A set of Action-specific tags that are reported together with the measurement results to a Collector.
   */
  public tag: string[];

  /**
   * A set of Suppression tags that are used to select Actions to be suppressed.
   */
  public 'suppression-tag': string[];

  /**
   * The current state of the Action (One of: enabled, disabled, running, suppressed).
   */
  public state: LmapState;

  /**
   * The amount of secondary storage (e.g., allocated in a file system)
   * holding temporary data allocated to the Schedule in bytes.
   * This object reports the amount of allocated physical storage and not the storage used by logical data records.
   */
  public storage: number;

  /**
   * Number of invocations of this Action.
   * This counter does not include suppressed invocations or invocations that were prevented
   * due to an overlap with a previous invocation of this Action.
   */
  public invocations: number;

  /**
   * Number of suppressed executions of this Action.
   */
  public suppressions: number;

  /**
   * Number of executions prevented due to overlaps with a previous invocation of this Action.
   */
  public overlaps: number;

  /**
   * Number of failed executions of this Action.
   */
  public failures: number;

  /**
   * The date and time of the last invocation of this Action.
   */
  public 'last-invocation': string; // TODO: change back to Date

  /**
   * The date and time of the last completion of this Action.
   */
  public 'last-completion': string; // TODO: change back to Date

  /**
   * The status code returned by the last execution of this Action (with 0 indicating successful execution).
   */
  public 'last-status': number;

  /**
   * The status message produced by the last execution of this Action.
   */
  public 'last-message': string;

  /**
   * The date and time of the last failed completion of this Action.
   */
  public 'last-failed-completion': string; // TODO: change back to Date

  /**
   * The status code returned by the last failed execution of this Action.
   */
  public 'last-failed-status': number;

  /**
   * The status message produced by the last failed execution of this Action.
   */
  public 'last-failed-message': string;
}
