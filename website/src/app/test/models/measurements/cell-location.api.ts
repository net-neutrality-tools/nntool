import {GeoLocation} from '../api/request-info.api';
import {PointInTimeValueAPI} from './point-in-time-value.api';
import {MeasurementResultNetworkPointInTimeAPI} from './measurement-result-network-point-in-time.api';

export class CellLocationAPI {

    /**
     * Contains the cell-ID, if available.
     */
    cell_id: number;

    /**
     * Contains the area code (e.g. location area code (GSM), tracking area code (LTE)), if available.
     */
    area_code: number;

    /**
     * Time and date the cell location information was captured (UTC).
     */
    time: string; // TODO: change back to Date

    /**
     * Contains the primary scrambling code, if available.
     */
    primary_scrambling_code: number;

    /**
     * Contains the ARFCN (Absolute Radio Frequency Channel Number) (e.g. 16-bit GSM ARFCN or 18-bit LTE EARFCN), if available.
     */
    arfcn: number;

    /**
     * Relative time in nanoseconds (to measurement begin).
     */
    relative_time_ns: number;

    /**
     * Geographic location latitude of this cell.
     */
    latitude: number;

    /**
     * Geographic location longitude of this cell.
     */
    longitude: number;

}

