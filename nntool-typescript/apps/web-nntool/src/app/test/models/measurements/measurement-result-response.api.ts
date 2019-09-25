import { BasicResponseAPI } from '../basic-response.api';

export class MeasurementResultResponseAPI extends BasicResponseAPI {
  /**
   * The UUIDv4 identifier of the measurement.
   */
  public uuid: string;

  /**
   * An UUIDv4 identifier that is used to find this measurement in an open-data context.
   */
  public openDataUuid: string;
}
