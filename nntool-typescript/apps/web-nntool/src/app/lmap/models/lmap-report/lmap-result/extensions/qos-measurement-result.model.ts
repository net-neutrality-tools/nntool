import { SubMeasurementResult } from './sub-measurement-result.model';

export class QoSMeasurementResult extends SubMeasurementResult {
  public results: Array<{ [key: string]: any }>;

  public key_to_translation_map: { [key: string]: string };

  public qos_type_to_description_map: { [key: string]: QoSTypeDescription };
}

export class QoSTypeDescription {

  public name: string;

  public description: string;

  public icon: string;
}
