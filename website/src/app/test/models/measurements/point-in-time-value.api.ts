import {GeoLocation} from '../api/request-info.api';

export class PointInTimeValueAPI<T> {

    /**
     * The relative time in nanoseconds to the test start.
     */
    relative_time_ns: number;

    /**
     * The value recorded at this point in time.
     */
    value: T;

}

