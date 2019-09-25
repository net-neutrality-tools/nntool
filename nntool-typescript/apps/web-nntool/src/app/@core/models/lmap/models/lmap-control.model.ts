import { LmapAgent } from './lmap-control/lmap-agent.model';
import { LmapCapability } from './lmap-control/lmap-capability.model';
import { LmapEvent } from './lmap-control/lmap-event.model';
import { LmapSchedule } from './lmap-control/lmap-schedule.model';
import { LmapSuppression } from './lmap-control/lmap-suppression.model';
import { LmapTask } from './lmap-control/lmap-task.model';
import { RequestInfoAPI } from '../../../../test/models/api/request-info.api';

export class LmapControl {
  /**
   * Agent capabilities including a list of supported Tasks.
   */
  public capabilities: LmapCapability;

  /**
   * Configuration of parameters affecting the whole Measurement Agent.
   */
  public agent: LmapAgent;

  /**
   * Configuration of LMAP Tasks.
   */
  public tasks: LmapTask[];

  /**
   * Configuration of LMAP Schedules. Schedules control which Tasks are executed by the LMAP implementation.
   */
  public schedules: LmapSchedule[];

  /**
   * Suppression information to prevent Schedules or certain Actions from starting.
   */
  public suppressions: LmapSuppression[];

  /**
   * Configuration of LMAP events.
   * Implementations may be forced to delay acting upon the occurrence of events in the face of local constraints.
   *  An Action triggered by an event therefore should not rely on the accuracy provided by the scheduler implementation.
   */
  public events: LmapEvent[];

  /**
   * Additional information that is sent by client alongside the request. This contains most information from ClientInfo.
   */
  public 'additional-request-info': RequestInfoAPI;
}
