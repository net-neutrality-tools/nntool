import { SubMeasurementResult } from './sub-measurement-result.model';

export class QoSMeasurementResult extends SubMeasurementResult {
  public results: Array<{ [key: string]: any }>;
}
