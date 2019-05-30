import {LmapFunctionAPI} from './lmap-function.api';

export class LmapCapabilityTaskAPI {

    /**
     * The unique name of a Task capability.
     * Refers to the {@link LmapTaskDto#getName()} and needs be the exact same in order to match.
     */
    name: string;

    /**
     * A list of entries in a registry identifying functions.
     */
    function: LmapFunctionAPI[];

    /**
     * The (local) program to invoke in order to execute the Task.
     * If this leaf is not set, then the system will try to identify a suitable program based on the registry information present.
     */
    program: string;

    /**
     * A short description of the software implementing the Task.
     * This should include the version number of the Measurement Task software.
     */
    version: string;
}

