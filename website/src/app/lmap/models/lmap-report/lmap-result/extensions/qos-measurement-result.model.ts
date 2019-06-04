import {SubMeasurementResult} from './sub-measurement-result.model';

export class QoSMeasurementResult extends SubMeasurementResult {
    results: {[key: string]: any}[];
}
