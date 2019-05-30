import {GeoLocation, MeasurementAgentType} from '../api/request-info.api';

export class BriefSubMeasurement {
    start_test: string; // TODO: change back to Date

    duration_ns: number;
}

export class BriefDeviceInfo {
    /**
     * Device code name.
     */
    device_code_name: string;

    /**
     * The device name that is commonly known to users (e.g. Google Pixel).
     */
    device_full_name: string;

    /**
     * Device operating system name.
     */
    os_name: string;

    /**
     * Device operating system version.
     */
    os_version: string;

    /**
     * Average CPU usage during the measurement.
     */
    avg_cpu_usage: number;

    /**
     * Average Memory usage during the measurement.
     */
    avg_mem_usage: number;
}


export class BriefMeasurementResponseAPI {

    // Measurement

    /**
     * The UUIDv4 identifier of the measurement object.
     */
    uuid: string;

    /**
     * Overall start time in UTC.
     */
    start_time: string; // TODO: change back to Date

    /**
     * Overall duration of all sub measurements.
     */
    duration_ns: number;

// GeoLocationInfo

    /**
     * The first accurate GeoLocation i.e. the location where the measurement was started.
     */
    first_accurate_geo_location: GeoLocation;

// AgentInfo

    /**
     * @see AgentType
     */
    type: MeasurementAgentType;

// DeviceInfo

    /**
     * @see BriefDeviceInfo
     */
    device_info: BriefDeviceInfo;

// NetworkInfo

    /**
     * Network type id (@see NetworkType).
     */
    network_type_id: number;

    /**
     * Network type name (@see NetworkType).
     */
    network_type_name: string;

// SubMeasurement

    /**
     * Map that contains available infor mation for each measurement type (Speed, QoS).
     * If map misses speed then no speed measurement was done, likewise for QoS, ...
     */
    measurements: {SPEED: BriefSubMeasurement, QOS: BriefSubMeasurement};


}

