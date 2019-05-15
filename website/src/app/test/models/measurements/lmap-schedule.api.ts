import {LmapStateAPI} from "./lmap-state.api";
import {LmapActionAPI} from "./lmap-action.api";
import {LmapStopAPI} from "./lmap-stop.api";

export enum ExecutionMode {
    SEQUENTIAL = 'SEQUENTIAL',	// The Actions of the Schedule are executed sequentially.
    PARALLEL = 'PARALLEL', // The Actions of the Schedule are executed concurrently.
    PIPELINED = 'PIPELINED' // The Actions of the Schedule are executed in a pipelined mode.
    // Output created by an Action is passed as input to the subsequent Action.
}

export class LmapScheduleAPI {

    /**
     * The locally unique, administratively assigned name for this Schedule.
     */
    name: string;

    /**
     * The event source controlling the start of the scheduled Actions.
     * Referencing the {@link LmapEventDto#getName()} of an Action.
     */
    start: string;

    /**
     * This choice contains optional leafs that control the graceful forced termination of scheduled Actions.
     * When the end has been reached, the scheduled Actions should be forced to terminate the measurements.
     * This may involve being active some additional time in order to properly finish the Action's activity
     * (e.g., waiting for any messages that are still outstanding).
     * If set to a {@link LmapStopDurationDto} it will behave like a typical
     * timeout set for the execution of this schedule.
     */
    stop: LmapStopAPI;

    /**
     * The execution mode of this Schedule determines in which order the Actions of the Schedule are executed.
     * Supported values are: sequential, pipelined, parallel.
     */
    "execution-mode": ExecutionMode;

    /**
     * A set of Schedule-specific tags that are reported together with the measurement results to a Collector.
     */
    tag: string[];

    /**
     * A set of Suppression tags that are used to select Actions to be suppressed.
     */
    "suppression-tag": string[];

    /**
     * The current state of the Schedule (One of: enabled, disabled, running, suppressed).
     */
    state: LmapStateAPI;

    /**
     * The amount of secondary storage (e.g., allocated in a file system)
     * holding temporary data allocated to the Schedule in bytes.
     * This object reports the amount of allocated physical storage and not the storage used by logical data records.
     */
    storage: number;

    /**
     * Number of invocations of this Schedule.
     * This counter does not include suppressed invocations or invocations that were prevented
     * due to an overlap with a previous invocation of this Schedule.
     */
    invocations: number;

    /**
     * Number of suppressed executions of this Schedule.
     */
    suppressions: number;

    /**
     * Number of executions prevented due to overlaps with a previous invocation of this Schedule.
     */
    overlaps: number;

    /**
     * Number of failed executions of this Schedule. A failed execution is an execution
     * where at least one Action failed.
     */
    failures: number;

    /**
     * The date and time of the last invocation of this Schedule.
     */
    "last-invocation": string; // TODO change back to Date

    /**
     * An Action describes a Task that is invoked by the Schedule.
     * Multiple Actions are invoked according to the execution-mode of the Schedule.
     */
    action: LmapActionAPI[];

}

