import {LmapOptionAPI} from './lmap-option.api';
import {LmapConflictAPI} from './lmap-conflict.api';

/**
 * The status of a measurement.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
export enum Status {
    STARTED = 'STARTED',
    FINISHED = 'FINISHED',
    FAILED = 'FAILED',
    ABORTED = 'ABORTED'
}

export enum Reason {
    /**
     * Use this if the connection to the measurement server couldn't be established.
     */
    UNABLE_TO_CONNECT = 'UNABLE_TO_CONNECT',

    /**
     * Use this if the connection was lost during a measurement.
     */
    CONNECTION_LOST = 'CONNECTION_LOST',

    /**
     * Use this if the network category changed (e.g. from MOBILE to WIFI).
     */
    NETWORK_CATEGORY_CHANGED = 'NETWORK_CATEGORY_CHANGED',

    /**
     * Use this if the App was put to background on mobile devices.
     */
    APP_BACKGROUNDED = 'APP_BACKGROUNDED',

    /**
     * Use this if the user aborted the measurement.
     */
    USER_ABORTED = 'USER_ABORTED'
}

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

export class RttInfo {

    /**
     * List of all measured RTTs.
     */
    rtts: Rtt[];

    /**
     * The number of RTT packets to send, as instructed by the server.
     */
    requested_num_packets: number;

    /**
     * The actual number of sent RTT packets.
     */
    num_sent: number;

    /**
     * The actual number of received RTT packets.
     */
    num_received: number;

    /**
     * The actual number of failed RTT packets.
     */
    num_error: number;

    /**
     * The actual number of missing RTT packets.
     */

    num_missing: number;

    /**
     * The actual size of RTT packets.
     */
    packet_size: number;
}

export class SpeedMeasurementRawDataItem {
    /**
     * The stream id (numeric value starting from 0).
     */
    stream_id: number;

    /**
     * Relative time since start of the speed measurement.
     */
    relative_time_ns: number;

    /**
     * Bytes transmitted or received since start of the speed measurement.
     */
    bytes: number;
}

export class ConnectionInfo {
    /**
     * The address of the measurement server.
     */
    address: string;

    /**
     * Port used for the communication.
     */
    port: number;

    /**
     * Indicates if the communication with the measurement server will be encrypted.
     */
    encrypted: boolean;

    /**
     * Cryptographic protocol and cipher suite used for encrypted communication, if available.
     * E.g. TLSv1.2 (TLS_RSA_WITH_AES_128_GCM_SHA256).
     */
    encryption_info: string;

    /**
     * Contains information about total bytes transferred during the speed measurement, as reported by the client's interface, if available.
     * Only used for displaying to the client.
     */
    client_interface_total_traffic: Traffic;

    /**
     * Contains information about bytes transferred during the download measurement, as reported by the client's interface, if available.
     * Only used for displaying to the client.
     */
    client_interface_download_measurement_traffic: Traffic;

    /**
     * Contains information about bytes transferred during the upload measurement, as reported by the client's interface, if available.
     * Only used for displaying to the client.
     */
    client_interface_upload_measurement_traffic: Traffic;

    /**
     * @see NumStreamsInfo
     */
    num_streams_info: NumStreamsInfo;

    /**
     * Flag if TCP SACK (Selective Acknowledgement) is enabled/requested.
     * See <a href="https://tools.ietf.org/html/rfc2018">https://tools.ietf.org/html/rfc2018</a>.
     */
    tcp_opt_sack_requested: boolean;

    /**
     * Flag if the TCP window scale options are requested.
     */
    tcp_opt_wscale_requested: boolean;

    /**
     * Maximum Segment Size (MSS) value from the server-side.
     */
    server_mss: number;

    /**
     * Maximum Transmission Unit (MTU) value from the server-side.
     */
    server_mtu: number;

    /**
     * @see WebSocketInfo
     */
    web_socket_info_download: WebSocketInfo;

    /**
     * @see WebSocketInfo
     */
    web_socket_info_upload: WebSocketInfo;
}

export class Rtt {
    /**
     * Round trip time recorded in nanoseconds.
     */
    rtt_ns: number;

    /**
     * Relative time in nanoseconds (to test begin).
     */

    relative_time_ns: number;
}

export class Traffic {
    /**
     * Bytes received.
     */
    bytes_rx: number;

    /**
     * Bytes transmitted.
     */
    bytes_tx: number;
}

export class NumStreamsInfo {

    /**
     * The requested number of streams for the download measurement.
     */
    requested_num_streams_download: number;

    /**
     * The requested number of streams for the upload measurement.
     */
    requested_num_streams_upload: number;

    /**
     * The actual number of streams used by the download measurement.
     */
    actual_num_streams_download: number;

    /**
     * The actual number of streams used by the upload measurement.
     */
    actual_num_streams_upload: number;
}

export class WebSocketInfo {

    /**
     * Size of a transmitted frame over the WebSocket protocol.
     */
    frameSize: number;

    /**
     * Number of frames sent over the WebSocket protocol during measurement excluding slow-start phase.
     */
    frameCount: number;

    /**
     * Number of frames sent over the WebSocket protocol during measurement including slow-start phase.
     */
    frame_count_including_slow_start: number;

    /**
     * The overhead sent during the communication via the WebSocket protocol excluding slow-start phase.
     */
    overhead: number;

    /**
     * The overhead sent during the communication via the WebSocket protocol including slow-start phase.
     */
    overhead_per_frame_including_slow_start: number;

    /**
     * The overhead a single frame produces on average.
     */
    overhead_per_frame: number;
}

export class QoSMeasurementResult extends SubMeasurementResult {
    results: {[key: string]: any}[];
}

export class LmapResultAPI {

    /**
     * The name of the Schedule that produced the result.
     */
    schedule: string;

    /**
     * The name of the Action in the Schedule that produced the result.
     */
    action: string;

    /**
     * The name of the Task that produced the result.
     */
    task: string;

    /**
     * This container is a placeholder for runtime parameters defined in Task-specific data models
     * augmenting the base LMAP report data model.
     */
    parameters: object;

    /**
     * The list of options there were in use when the measurement was performed.
     * This list must include both the Task-specific options as well as the Action-specific options.
     */
    option: LmapOptionAPI[];

    /**
     * A tag contains additional information that is passed with the result record to the Collector.
     * This is the joined set of tags defined for the Task object, the Schedule object, and the Action object.
     * A tag can be used to carry the Measurement Cycle ID.
     */
    tag: string[];

    /**
     * The date and time of the event that triggered the Schedule of the Action
     * that produced the reported result values.
     * The date and time does not include any added randomization.
     */
    event: string; // TODO: change back to Date

    /**
     * The date and time when the Task producing this result started.
     */
    start: string; // TODO: change back to Date

    /**
     * The date and time when the Task producing this result finished.
     */
    end: string; // TODO: change back to Date

    /**
     * The optional cycle number is the time closest to the time reported in the event leaf
     * that is a multiple of the cycle-interval of the event that triggered the execution of the Schedule.
     * The value is only present if the event that triggered the execution of the Schedule has a defined cycle-interval.
     */
    'cycle-number': string;

    /**
     * The status code returned by the execution of this Action.
     *
     * A status code returned by the execution of a Task.  Note
     * that the actual range is implementation dependent, but it
     * should be portable to use values in the range 0..127 for
     * regular exit codes.  By convention, 0 indicates successful
     * termination.  Negative values may be used to indicate
     * abnormal termination due to a signal; the absolute value
     * may identify the signal number in this case.
     */
    status: number;

    /**
     * The names of Tasks overlapping with the execution of the Task that has produced this result.
     */
    conflict: LmapConflictAPI[];

    /**
     * A list of results. Replaces the table list from LMAP
     */
    results: (SpeedMeasurementResult | QoSMeasurementResult)[];

}

