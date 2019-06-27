import {LmapEventType} from '../lmap-event-type.api';
import {LmapEventTypeEventType} from './lmap-event-type-event-type.enum';

export class LmapEventTypePeriodic extends LmapEventType {

    /**
     * Type identifier of the given event.
     */
    type: LmapEventTypeEventType = LmapEventTypeEventType.PERIODIC;

    interval: number;

    start?: string; // TODO: Change back to Date

    end: string; // TODO: Change back to Date
}

