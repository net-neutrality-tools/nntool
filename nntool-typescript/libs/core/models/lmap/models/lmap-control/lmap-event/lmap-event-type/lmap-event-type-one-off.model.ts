import { LmapEventType } from '../lmap-event-type.api';
import { LmapEventTypeEventType } from './lmap-event-type-event-type.enum';

export class LmapEventTypeOneOff extends LmapEventType {
  /**
   * Type identifier of the given event.
   */
  public type: LmapEventTypeEventType = LmapEventTypeEventType.ONE_OFF;

  public time: string; // TODO: change back to Date
}
