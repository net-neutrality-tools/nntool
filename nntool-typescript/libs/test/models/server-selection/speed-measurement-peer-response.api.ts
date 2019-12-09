import { BasicResponseAPI } from '../basic-response.api';
import { SpeedMeasurementPeer } from './speed-measurement-peer';

export class SpeedMeasurementPeerResponseAPI extends BasicResponseAPI {
  public peers: SpeedMeasurementPeer[];
}
