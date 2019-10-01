import { LmapEventTypeEventType } from './lmap-event-type/lmap-event-type-event-type.enum';

export abstract class LmapEventType {
  /**
   * Type identifier of the given event.
   */
  public type: LmapEventTypeEventType;
}
