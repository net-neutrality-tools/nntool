import {RequestInfoAPI} from '../api/request-info.api';
import {LmapCapabilityAPI} from './lmap-capability.api';
import {LmapAgentAPI} from './lmap-agent.api';
import {LmapTaskAPI} from './lmap-task.api';
import {LmapScheduleAPI} from './lmap-schedule.api';
import {LmapSuppressionAPI} from './lmap-suppression.api';
import {LmapEventAPI} from './lmap-event.api';

export class LmapControlAPI {

    /**
     * Agent capabilities including a list of supported Tasks.
     */
    capabilities: LmapCapabilityAPI;

    /**
     * Configuration of parameters affecting the whole Measurement Agent.
     */
    agent: LmapAgentAPI;

    /**
     * Configuration of LMAP Tasks.
     */
    tasks: LmapTaskAPI[];

    /**
     * Configuration of LMAP Schedules. Schedules control which Tasks are executed by the LMAP implementation.
     */
    schedules: LmapScheduleAPI[];

    /**
     * Suppression information to prevent Schedules or certain Actions from starting.
     */
    suppressions: LmapSuppressionAPI[];

    /**
     * Configuration of LMAP events.
     * Implementations may be forced to delay acting upon the occurrence of events in the face of local constraints.
     *  An Action triggered by an event therefore should not rely on the accuracy provided by the scheduler implementation.
     */
    events: LmapEventAPI[];

    /**
     * Additional information that is sent by client alongside the request. This contains most information from ClientInfo.
     */
    'additional-request-info': RequestInfoAPI;
}

