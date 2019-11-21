import { Component, Inject, AfterViewInit, NgZone, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PortBlockingTestTypeEnum } from '../tests-implementation/port-blocking/enums/port-blocking-test-type';
import { PortBlockingTestConfig } from '../tests-implementation/port-blocking/port-blocking-test-config';
import { PortBlockingTestImplementation } from '../tests-implementation/port-blocking/port-blocking-test-implementation';
import { PortBlockingTestState } from '../tests-implementation/port-blocking/port-blocking-test-state';
import { BarUIComponent } from '../tests-ui/bar/bar-ui';
import { BarUIState } from '../tests-ui/bar/bar-ui-state';
import { BarUIShowableTestTypeEnum } from '../tests-ui/bar/enums/bar-ui-showable-test-type.enum';
import { ConfigService } from '../../@core/services/config.service';
import { WINDOW } from '../../@core/services/window.service';
import { LoopSettingsImplementation } from '../tests-implementation/loop-settings/loop-settings-implementation';
import { LoopSettingsTestState } from '../tests-implementation/loop-settings/loop-settings-test-state';

@Component({
    // needs to be mentioned here, but also mentioned in gauge-ui.ts for reference
    templateUrl: '../tests-ui/loop/loop-settings.template.html',
    selector: 'app-loop-settings'
})
export class LoopSettingsComponent extends BarUIComponent<
LoopSettingsImplementation,
PortBlockingTestConfig,
LoopSettingsTestState
> implements OnDestroy {
    // TODO: rethink DI in this use case, testImplementation should not be one instance, if there were more than one test at once
    // TODO: Remove this constructor when DI on generic type figured out
    constructor(
        testImplementation: LoopSettingsImplementation,
        configService: ConfigService,
        translateService: TranslateService,
        @Inject(WINDOW) window: Window,
        public zone: NgZone
    ) {
        super(testImplementation, configService, translateService, window);
        this.testImpl = testImplementation;
    }

    private testImpl: LoopSettingsImplementation;


    public onOnlyReInit() {
        if (this.testImpl.curRepetitions > 0) {
            this.startTimerForNextTest();
        }
    }

    ngOnDestroy() {
        this.testImpl.onDestroy();
    }

    protected testStateToUIState = (state: PortBlockingTestState): BarUIState => {
        const barUIState: BarUIState = new BarUIState();
        barUIState.types = [];
        /*
        state.types.forEach(
            (type: {
                key: PortBlockingTestTypeEnum;
                ports: Array<{
                    number: number;
                    reachable: boolean;
                    finished: boolean;
                }>;
            }) => {
                const mappedType: {
                    key: BarUIShowableTestTypeEnum;
                    ports: Array<{
                        number: number;
                        reachable: boolean;
                        finished: boolean;
                    }>;
                } = { key: undefined, ports: undefined };
                mappedType.key = BarUIShowableTestTypeEnum[PortBlockingTestTypeEnum[type.key]];
                mappedType.ports = type.ports;
                barUIState.types.push(mappedType);
            }
        );*/
        return barUIState;
    };

    public startTimerForNextTest() {
        if (this.testImpl.curRepetitions >= this.testImpl.loopModeConfig.numRepetitions) {
            console.log("REACHED END OF LOOP MODE!");
            return;
        }

        console.log("START TIMER FOR NEXT TEST!");

        let timeLeft = this.testImpl.loopModeConfig.timeBetweenRepetitions * 60;
        this.testImpl.timeLeftString = this.toHHMMSS(timeLeft);
        let interval = setInterval(() => {
          if(timeLeft > 1) {
            timeLeft--;
          } else {
            clearInterval(interval);
            timeLeft = 0;
            this.requestStart();
          }
    
          this.testImpl.timeLeftString = this.toHHMMSS(timeLeft);
          
          this.zone.run(() => {

          });

        }, 1000)
      }
    
      private toHHMMSS(timeLeft) {
        var date = new Date(null);
        date.setSeconds(timeLeft); // specify value for SECONDS here
        return date.toISOString().substr(11, 8);
      }
    
}
