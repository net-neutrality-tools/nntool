import {LmapCapabilityTaskAPI} from './lmap-capability-task.api';

export class LmapCapabilityAPI {

    /**
     * A short description of the software implementing the Measurement Agent.
     * This should include the version number of the Measurement Agent software.
     */
    version: string;

    /**
     * An optional unordered set of tags that provide additional information about the capabilities of the Measurement Agent.
     */
    tag: string[];

    /**
     * A list of Tasks that the Measurement Agent supports.
     */
    tasks: LmapCapabilityTaskAPI[];
}

