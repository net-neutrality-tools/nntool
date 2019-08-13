import {RequestInfoAPI} from '../../test/models/api/request-info.api';
import {TimeBasedResultAPI} from '../../test/models/measurements/time-based-result.api';
import {LmapResult} from './lmap-report/lmap-result.model';

export class LmapReport {

    /**
     * The date and time when this result report was sent to a Collector.
     */
    date: string; // TODO: change back to Date

    /**
     * The agent-id of the agent from which this report originates.
     */
    'agent-id'?: string;

    /**
     * The group-id of the agent from which this report originates.
     */
    'group-id'?: string;

    /**
     * The measurement-point of the agent from which this report originates.
     */
    'measurement-point'?: string;

    /**
     * The list of Tasks for which results are reported.
     */
    result: LmapResult[];

    /**
     * Additional information that is sent by client alongside the request. This contains most information from ClientInfo.
     */
    additional_request_info: RequestInfoAPI;

    /**
     * This module defines a data model for reporting time based results from Measurement Agents.
     */
    time_based_result: TimeBasedResultAPI;

}

