import {EventTypeEnum, LmapEventTypeAPI} from "./lmap-event-type.api";

export class LmapImmediateEvent extends LmapEventTypeAPI {

    /**
     * Type identifier of the given event.
     */
    type: EventTypeEnum = EventTypeEnum.IMMEDIATE;

}

