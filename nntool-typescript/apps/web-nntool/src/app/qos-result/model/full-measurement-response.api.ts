export class FullMeasurementResponse {

    uuid: string;

    open_data_uuid: string;

    system_uuid: string;

    start_time: string;

    end_time: string;

    duration_ns: number;

    measurements: SingleMeasurement;
}

export class SingleMeasurement {
    QOS: QoSResponse;
    SPEED: SpeedResponse;

}

export class QoSResponse {
    implausible: boolean;
    version_protocol: string;
    version_library: string;
    relative_start_time_ns: string;
    relative_end_time_ns: string;
    start_time: string;
    end_time: string;
    duration_ns: string;
    status: string;
    reason: string;

    results: QoSResult[];
    key_to_translation_map: Map<string, string>;
    qos_type_to_description_map: Map<string, QoSTypeDescription>; //TODO: make key an enum
}

export class QoSResult {
    type: string;
    summary: string;
    description: string;
    evaluation_count: number;
    success_count: number;
    failure_count: number;
    implausible: boolean;
    evaluation_keys: Map<string, string>;
    result_keys: Map<string, string>;
}

export class QoSTypeDescription {
    name: string;
    description: string;
    icon: string;
}

export class SpeedResponse {
    implausible: boolean;
    version_protocol: string;
    version_library: string;
    relative_start_time_ns: number;
    relative_end_time_ns: number;
    start_time: string;
    end_time: string;
    duration_ns: number;
    status: string;
    reason: string;
    throughput_avg_download_bps: number;
    throughput_avg_download_log: number;
    throughput_avg_upload_bps: number;
    throughput_avg_upload_log: number;
    bytes_download: number;
    bytes_download_including_slow_start: number;
    bytes_upload: number;
    bytes_upload_including_slow_start: number;
    requested_duration_download_ns: number;
    requested_duration_upload_ns: number;
    duration_rtt_ns: number;
    duration_download_ns: number;
    duration_upload_ns: number;
    relative_start_time_rtt_ns: number;
    relative_start_time_download_ns: number;
    relative_start_time_upload_ns: number;
    rtt_info: any;
    download_raw_data: any;
    upload_raw_data: any;
    connection_info: any;
}