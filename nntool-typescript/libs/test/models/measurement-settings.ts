import { MeasurementTypeParameters } from '../../core/models/lmap/models/shared/lmap-option.model';

export class MeasurementSettings {
  public speedConfig: MeasurementTypeParameters;
  public serverPort: string;
  public encryption: boolean;
  public serverAddress: string;
  public collectorAddress: string;
}
