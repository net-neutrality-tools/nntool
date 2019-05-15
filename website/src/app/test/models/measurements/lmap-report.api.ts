import {RequestInfoAPI} from "../api/request-info.api";
import {TimeBasedResultAPI} from "./time-based-result.api";
import {LmapResultAPI} from "./lmap-result.api";

export class LmapReportAPI {

    /**
     * The date and time when this result report was sent to a Collector.
     */
    date: string; // TODO: change back to Date

    /**
     * The agent-id of the agent from which this report originates.
     */
    "agent-id": string;

    /**
     * The group-id of the agent from which this report originates.
     */
    "group-id": string;

    /**
     * The measurement-point of the agent from which this report originates.
     */
    "measurement-point": string;

    /**
     * The list of Tasks for which results are reported.
     */
    result: LmapResultAPI[];

    /**
     * Additional information that is sent by client alongside the request. This contains most information from ClientInfo.
     */
    additional_request_info: RequestInfoAPI;

    /**
     * This module defines a data model for reporting time based results from Measurement Agents.
     */
    time_based_result: TimeBasedResultAPI;

}

