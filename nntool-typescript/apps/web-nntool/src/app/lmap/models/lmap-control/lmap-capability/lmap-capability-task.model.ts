import { LmapFunction } from '../../shared/lmap-function.model';

export class LmapCapabilityTask {
  /**
   * The unique name of a Task capability.
   * Refers to the {@link LmapTaskDto#getName()} and needs be the exact same in order to match.
   */
  public name: string;

  /**
   * A list of entries in a registry identifying functions.
   */
  public function: LmapFunction[];

  /**
   * The (local) program to invoke in order to execute the Task.
   * If this leaf is not set, then the system will try to identify a suitable program based on the registry information present.
   */
  public program?: string;

  /**
   * A short description of the software implementing the Task.
   * This should include the version number of the Measurement Task software.
   */
  public version?: string;

  /**
   * The measurement peer identifier the agent wishes to measure against for this task.
   */
  public selected_measurement_peer_identifier?: string;
}
