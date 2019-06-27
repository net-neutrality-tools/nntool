import {LmapStop} from './lmap-stop.model';

export class LmapStopEnd extends LmapStop {
    /**
     * The event source controlling the graceful forced termination of the scheduled Actions.
     */
    end?: string;
}

