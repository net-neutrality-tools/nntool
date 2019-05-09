import {SpeedTestImplementation} from "../tests-implementation/speed/speed-test-implementation";
import {GaugeUI} from "../tests-ui/gauge/gauge-ui";
import {GaugeUIState} from "../tests-ui/gauge/gauge-ui-state";
import {SpeedTestState} from "../tests-implementation/speed/speed-test-state";
import {Component} from "@angular/core";
import {SpeedTestStateEnum} from "../tests-implementation/speed/enums/speed-test-state.enum";
import {GaugeUIStateEnum} from "../tests-ui/gauge/enums/gauge-ui-state.enum";
import {ConfigService} from "../../services/config.service";
import {TranslateService} from "@ngx-translate/core";
import {SpeedTestConfig} from "../tests-implementation/speed/speed-test-config";

@Component({
    // needs to be mentioned here, but also mentioned in gauge-ui.ts for reference
    templateUrl: "./app/testing/tests-ui/gauge/gauge-ui.template.html",
    selector: 'speed-test-gauge'
})
export class SpeedTestGauge extends GaugeUI<SpeedTestImplementation, SpeedTestConfig, SpeedTestState> {


    // TODO: rethink DI in this use case, testImplementation should not be one instance, if there were more than one test at once
    // TODO: Remove this constructor when DI on generic type figured out
    constructor(testImplementation: SpeedTestImplementation, configService: ConfigService, translateService: TranslateService) {
        super(testImplementation, configService, translateService);
    }

    protected testStateToUIState = (state: SpeedTestState): GaugeUIState => {
        const gaugeUIState: GaugeUIState = new GaugeUIState();
        gaugeUIState.gaugeUIState         = GaugeUIStateEnum[SpeedTestStateEnum[state.speedTestState]];
        gaugeUIState.uuid                   = state.uuid;
        gaugeUIState.progress               = state.progress;
        gaugeUIState.totalProgress          = state.totalProgress;
        gaugeUIState.ping                   = state.ping;
        gaugeUIState.downMBit               = state.downMBit;
        gaugeUIState.downBit                = state.downBit;
        gaugeUIState.upMBit                 = state.upMBit;
        gaugeUIState.upBit                  = state.upBit;
        gaugeUIState.serverName             = state.serverName;
        gaugeUIState.remoteIp               = state.remoteIp;
        gaugeUIState.provider               = state.provider;
        gaugeUIState.location               = null;
        if (state.location) {
            gaugeUIState.location               = {latitude: null, longitude: null};
            gaugeUIState.location.latitude      = state.location.latitude;
            gaugeUIState.location.longitude     = state.location.longitude;
        }
        gaugeUIState.device                 = state.device;
        gaugeUIState.technology             = state.technology;
        return gaugeUIState;
    }
}