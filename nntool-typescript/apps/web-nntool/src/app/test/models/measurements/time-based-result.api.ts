import {GeoLocation} from '../api/request-info.api';
import {PointInTimeValueAPI} from './point-in-time-value.api';
import {MeasurementResultNetworkPointInTimeAPI} from './measurement-result-network-point-in-time.api';
import {SignalAPI} from './signal.api';
import {CellLocationAPI} from './cell-location.api';

export class TimeBasedResultAPI {

    /**
     * Start date and time for this measurement. Date and time is always stored as UTC.
     */
    start_time: string; // TODO: change back to Date

    /**
     * End date and time for this measurement. Date and time is always stored as UTC.
     */
    end_time: string; // TODO: change back to Date

    /**
     * Overall duration of this measurement.
     */
    duration_ns: number;

// GeoLocationInfo

    /**
     * List of all captured geographic locations.
     */
    geo_locations: GeoLocation[];

// AgentInfo

    // -> everything already submitted by ApiRequestInfo

// DeviceInfo

    // -> everything already submitted by ApiRequestInfo

// _ OperatingSystemInfo

    /**
     * CPU usage during the test, if available.
     */
    cpu_usage: PointInTimeValueAPI<number>[];

    /**
     * Memory usage during the test, if available.
     */
    mem_usage: PointInTimeValueAPI<number>[];

// NetworkInfo

// _ EmbeddedNetworkType

    /**
     * Contains all relevant network information of a single point in time.
     * @see NetworkPointInTime
     */
    network_points_in_time: MeasurementResultNetworkPointInTimeAPI[];

// _ CellLocationInfo

    /**
     * List of captured cell information.
     */
    cell_locations: CellLocationAPI[];

// _ SignalInfo

    /**
     * List of captured signal information.
     */
    signals: SignalAPI[];

}

