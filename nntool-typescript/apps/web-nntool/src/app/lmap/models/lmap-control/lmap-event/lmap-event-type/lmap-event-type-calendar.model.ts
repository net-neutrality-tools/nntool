import { LmapEventType } from '../lmap-event-type.api';
import { LmapEventTypeEventType } from './lmap-event-type-event-type.enum';

export class LmapEventTypeControllerConnected extends LmapEventType {
  /**
   * Type identifier of the given event.
   */
  public type: LmapEventTypeEventType = LmapEventTypeEventType.CALENDAR;

  public month: string;

  public 'day-of-month': string;

  public 'day-of-week': string;

  public hour: number;

  public minute: number;

  public second: number;

  public 'timezone-offset'?: number;

  public start?: string; // TODO: Change back to Date

  public end: string; // TODO: Change back to Date
}
