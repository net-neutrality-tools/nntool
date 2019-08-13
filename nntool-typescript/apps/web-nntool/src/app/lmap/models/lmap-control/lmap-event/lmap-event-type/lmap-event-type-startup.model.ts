import {LmapEventType} from '../lmap-event-type.api';
import {LmapEventTypeEventType} from './lmap-event-type-event-type.enum';

export class LmapEventTypeStartup extends LmapEventType {

    /**
     * Type identifier of the given event.
     */
    type: LmapEventTypeEventType = LmapEventTypeEventType.STARTUP;

}

