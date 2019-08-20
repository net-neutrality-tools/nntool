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
