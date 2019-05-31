import {Status} from './status.enum';
import {Reason} from './reason.enum';

export abstract class SubMeasurementResult {
    /**
     * Start time in nanoseconds relative to the start time of the overall measurement object.
     */
    relative_start_time_ns: number;

    /**
     * End time in nanoseconds relative to the end time of the overall measurement object.
     */
    relative_end_time_ns: number;

    // MeasurementStatusInfo

    /**
     * @see StatusDto
     */
    status: Status;

    /**
     * @see ReasonDto
     */
    reason: Reason;

    deserialize_type: string;
}
