import {LmapEventType} from '../lmap-event-type.api';
import {LmapEventTypeEventType} from './lmap-event-type-event-type.enum';

export class LmapEventTypeControllerConnected extends LmapEventType {

    /**
     * Type identifier of the given event.
     */
    type: LmapEventTypeEventType = LmapEventTypeEventType.CALENDAR;

    month: string;

    'day-of-month': string;

    'day-of-week': string;

    hour: number;

    minute: number;

    second: number;

    'timezone-offset'?: number;

    start?: string; // TODO: Change back to Date

    end: string; // TODO: Change back to Date
}

