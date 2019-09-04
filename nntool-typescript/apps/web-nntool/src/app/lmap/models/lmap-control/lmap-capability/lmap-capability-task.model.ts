import {LmapFunction} from '../../shared/lmap-function.model';

export class LmapCapabilityTask {

    /**
     * The unique name of a Task capability.
     * Refers to the {@link LmapTaskDto#getName()} and needs be the exact same in order to match.
     */
    name: string;

    /**
     * A list of entries in a registry identifying functions.
     */
    function: LmapFunction[];

    /**
     * The (local) program to invoke in order to execute the Task.
     * If this leaf is not set, then the system will try to identify a suitable program based on the registry information present.
     */
    program?: string;

    /**
     * A short description of the software implementing the Task.
     * This should include the version number of the Measurement Task software.
     */
    version?: string;

    /**
	 * The measurement peer identifier the agent wishes to measure against for this task.
	 */
    selected_measurement_peer_identifier?: string;
}
