export enum EventTypeEnum {
    IMMEDIATE
}

export abstract class LmapEventTypeAPI {

    /**
     * Type identifier of the given event.
     */
    type: EventTypeEnum;

}

