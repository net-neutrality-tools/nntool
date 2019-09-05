import { Component, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { SpeedTestStateEnum } from '../tests-implementation/speed/enums/speed-test-state.enum';
import { SpeedTestConfig } from '../tests-implementation/speed/speed-test-config';
import { SpeedTestImplementation } from '../tests-implementation/speed/speed-test-implementation';
import { SpeedTestState } from '../tests-implementation/speed/speed-test-state';
import { GaugeUIStateEnum } from '../tests-ui/gauge/enums/gauge-ui-state.enum';
import { GaugeUIComponent } from '../tests-ui/gauge/gauge-ui';
import { GaugeUIState } from '../tests-ui/gauge/gauge-ui-state';
import { ConfigService } from '../../@core/services/config.service';
import { WINDOW } from '../../@core/services/window.service';

@Component({
  // needs to be mentioned here, but also mentioned in gauge-ui.ts for reference
  templateUrl: '../tests-ui/gauge/gauge-ui.template.html',
  selector: 'app-speed-test-gauge'
})
export class SpeedTestGaugeComponent extends GaugeUIComponent<
SpeedTestImplementation,
SpeedTestConfig,
SpeedTestState
> {
  // TODO: rethink DI in this use case, testImplementation should not be one instance, if there were more than one test at once
  // TODO: Remove this constructor when DI on generic type figured out
  constructor(
    protected logger: NGXLogger,
    testImplementation: SpeedTestImplementation,
    configService: ConfigService,
    translateService: TranslateService,
    @Inject(WINDOW) window: Window
  ) {
    super(logger, testImplementation, configService, translateService, window);
  }

  protected testStateToUIState = (state: SpeedTestState): GaugeUIState => {
    const gaugeUIState = new GaugeUIState();

    gaugeUIState.gaugeUIState = GaugeUIStateEnum[SpeedTestStateEnum[state.speedTestState]];
    gaugeUIState.uuid = state.uuid;
    gaugeUIState.progress = state.progress;
    gaugeUIState.totalProgress = state.totalProgress;
    gaugeUIState.ping = state.ping ? Math.round(state.ping * 100) / 100 : undefined;
    gaugeUIState.downMBit = state.downMBit ? Math.round(state.downMBit * 100) / 100 : undefined;
    gaugeUIState.downBit = state.downBit ? Math.round(state.downBit * 100) / 100 : undefined;
    gaugeUIState.upMBit = state.upMBit ? Math.round(state.upMBit * 100) / 100 : undefined;
    gaugeUIState.upBit = state.upBit ? Math.round(state.upBit * 100) / 100 : undefined;
    gaugeUIState.serverName = state.serverName;
    gaugeUIState.remoteIp = state.remoteIp;
    gaugeUIState.provider = state.provider;
    gaugeUIState.location = null;

    if (state.location) {
      gaugeUIState.location = { latitude: null, longitude: null };
      gaugeUIState.location.latitude = state.location.latitude;
      gaugeUIState.location.longitude = state.location.longitude;
    }

    gaugeUIState.device = state.device;
    gaugeUIState.technology = state.technology;

    return gaugeUIState;
  };
}
