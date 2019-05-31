import {SubMeasurementResult} from './sub-measurement-result.model';
import {RttInfo} from './rtt-info.model';
import {SpeedMeasurementRawDataItem} from './speed-measurement-raw-data-item.model';
import {ConnectionInfo} from './connection-info.model';

export class SpeedMeasurementResult extends SubMeasurementResult {
    /**
     * Bytes received during the speed measurement (Download).
     */
    bytes_download: number;

    /**
     * Bytes received during the speed measurement (Download) with slow-start phase.
     */
    bytes_download_including_slow_start: number;

    /**
     * Bytes transferred during the speed measurement (Upload).
     */
    bytes_upload: number;

    /**
     * Bytes transferred during the speed measurement (Upload) with slow-start phase.
     */
    bytes_upload_including_slow_start: number;

    /**
     * Duration of the RTT measurement.
     */
    duration_rtt_ns: number;

    /**
     * Duration of the download measurement.
     */
    duration_download_ns: number;

    /**
     * Duration of the upload measurement.
     */
    duration_upload_ns: number;

    /**
     * Relative start time of the RTT measurement in nanoseconds.
     */
    relative_start_time_rtt_ns: number;

    /**
     * Relative start time of the download measurement in nanoseconds.
     */
    relative_start_time_download_ns: number;

    /**
     * Relative start time of the upload measurement in nanoseconds.
     */
    relative_start_time_upload_ns: number;

// RttInfo

    /**
     * @see RttInfoDto
     */
    rtt_info: RttInfo;

// SpeedMeasurementRawData

    /**
     * Contains a list of all captured byte transfers during the download speed measurement on the measurement agent.
     */
    download_raw_data: SpeedMeasurementRawDataItem[];

    /**
     * Contains a list of all captured byte transfers during the upload speed measurement on the measurement agent.
     */
    uploadRawData: SpeedMeasurementRawDataItem[];

// ConnectionInfo

    /**
     * @see ConnectionInfoDto
     */
    connection_info: ConnectionInfo;
}
