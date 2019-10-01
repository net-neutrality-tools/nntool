import { LmapEventType } from '../lmap-event-type.api';
import { LmapEventTypeEventType } from './lmap-event-type-event-type.enum';

export class LmapEventTypeControllerLost extends LmapEventType {
  /**
   * Type identifier of the given event.
   */
  public type: LmapEventTypeEventType = LmapEventTypeEventType.CONTROLLER_LOST;
}
