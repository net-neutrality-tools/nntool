import { LmapEventType } from '../lmap-event-type.api';
import { LmapEventTypeEventType } from './lmap-event-type-event-type.enum';

export class LmapEventTypePeriodic extends LmapEventType {
  /**
   * Type identifier of the given event.
   */
  public type: LmapEventTypeEventType = LmapEventTypeEventType.PERIODIC;

  public interval: number;

  public start?: string; // TODO: Change back to Date

  public end: string; // TODO: Change back to Date
}
