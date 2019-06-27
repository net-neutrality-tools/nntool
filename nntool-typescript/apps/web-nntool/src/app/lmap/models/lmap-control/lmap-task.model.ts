import {LmapFunction} from '../shared/lmap-function.model';
import {LmapOption} from '../shared/lmap-option.model';

export class LmapTask {

    /**
     * The unique name of a Task.
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
    program: string;

    /**
     * The list of Task-specific options.
     */
    option: LmapOption[];

    /**
     * A set of Task-specific tags that are reported together with the measurement results to a Collector.
     * A tag can be used, for example, to carry the Measurement Cycle ID.
     */
    tag: string[];
}

