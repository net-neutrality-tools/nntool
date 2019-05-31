import {UIState} from '../ui-state';
import {GaugeUIStateEnum} from './enums/gauge-ui-state.enum';

export class GaugeUIState extends UIState {
    gaugeUIState: GaugeUIStateEnum;
    uuid: string;
    progress: number;
    totalProgress: number;
    ping: number;
    downMBit: number;
    downBit: number;
    upMBit: number;
    upBit: number;
    serverName: string;
    remoteIp: string;
    provider: string;
    location: {
        latitude: string;
        longitude: string;
    };
    device: string;
    technology: string;

    apply = (other: GaugeUIState) => {
        this.gaugeUIState         = other.gaugeUIState;
        this.uuid                   = other.uuid;
        this.progress               = other.progress;
        this.totalProgress          = other.totalProgress;
        this.ping                   = other.ping;
        this.downMBit               = other.downMBit;
        this.downBit                = other.downBit;
        this.upMBit                 = other.upMBit;
        this.upBit                  = other.upBit;
        this.serverName             = other.serverName;
        this.remoteIp               = other.remoteIp;
        this.provider               = other.provider;
        this.location               = null;
        if (other.location) {
            this.location               = {latitude: null, longitude: null};
            this.location.latitude      = other.location.latitude;
            this.location.longitude     = other.location.longitude;
        }
        this.device                 = other.device;
        this.technology             = other.technology;
    }
}
