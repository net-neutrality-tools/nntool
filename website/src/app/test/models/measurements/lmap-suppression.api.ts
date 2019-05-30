export enum SuppressionState {
    ENABLED,
    DISABLED,
    ACTIVE
}

export class LmapSuppressionAPI {

    /**
     * The locally unique, administratively assigned name for this Suppression.
     */
    name: string;

    /**
     * The event source controlling the start of the Suppression period.
     * Referencing the {@link LmapEventDto#getName()} of an Action.
     */
    start: number;

    /**
     * The event source controlling the end of the Suppression period.
     * If not present, Suppression continues indefinitely.
     * Referencing the {@link LmapEventDto#getName()} of an Action.
     */
    end: number;

    /**
     * A set of Suppression match patterns.
     * The Suppression will apply to all Schedules (and their Actions)
     * that have a matching value in their suppression-tags
     * and to all Actions that have a matching value in their suppression-tags.
     */
    match: string[];

    /**
     * If 'stop-running' is true, running Schedules and Actions
     * matching the Suppression will be terminated when Suppression is activated.
     * If 'stop-running' is false, running Schedules and Actions will not be affected if Suppression is activated.
     */
    'stop-running': boolean;

    /**
     * The current state of the Suppression.
     * Possible values are: enabled, disabled, active.
     */
    state: SuppressionState;

}

