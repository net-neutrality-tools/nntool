/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { Component, Inject, AfterViewInit, NgZone, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PortBlockingTestTypeEnum } from '../tests-implementation/port-blocking/enums/port-blocking-test-type';
import { PortBlockingTestConfig } from '../tests-implementation/port-blocking/port-blocking-test-config';
import { PortBlockingTestImplementation } from '../tests-implementation/port-blocking/port-blocking-test-implementation';
import { PortBlockingTestState } from '../tests-implementation/port-blocking/port-blocking-test-state';
import { BarUIComponent } from '../tests-ui/bar/bar-ui';
import { BarUIState } from '../tests-ui/bar/bar-ui-state';
import { BarUIShowableTestTypeEnum } from '../tests-ui/bar/enums/bar-ui-showable-test-type.enum';
import { ConfigService } from '../../core/services/config.service';
import { WINDOW } from '../../core/services/window.service';
import { LoopSettingsImplementation } from '../tests-implementation/loop-settings/loop-settings-implementation';
import { LoopSettingsTestState } from '../tests-implementation/loop-settings/loop-settings-test-state';

@Component({
  // needs to be mentioned here, but also mentioned in gauge-ui.ts for reference
  templateUrl: '../tests-ui/loop/loop-settings.template.html',
  selector: 'app-loop-settings'
})
export class LoopSettingsComponent
  extends BarUIComponent<LoopSettingsImplementation, PortBlockingTestConfig, LoopSettingsTestState>
  implements OnDestroy {
  // TODO: rethink DI in this use case, testImplementation should not be one instance, if there were more than one test at once
  // TODO: Remove this constructor when DI on generic type figured out
  constructor(
    testImplementation: LoopSettingsImplementation,
    configService: ConfigService,
    translateService: TranslateService,
    @Inject(WINDOW) window: Window
  ) {
    super(testImplementation, configService, translateService, window);
    this.testImpl = testImplementation;
  }

  public testImpl: LoopSettingsImplementation;

  public showInvalidInputWarning: boolean = false;

  public readonly maxAllowedRepetitions: number = 500;

  public readonly maxTimeBetweenRepetitions: number = 1440;

  private timerInterval: any;

  public onOnlyReInit() {
    if (this.testImpl.curRepetitions > 0) {
      this.startTimerForNextTest();
    }
  }

  ngOnDestroy() {
    this.testImpl.alreadyStarted = false;
    this.testImpl.onDestroy();
    clearInterval(this.timerInterval);
  }

  protected testStateToUIState = (state: PortBlockingTestState): BarUIState => {
    return new BarUIState();
  };

  public requestStart() {
    if (
      this.testImpl.loopModeConfig.numRepetitions <= 0 ||
      this.testImpl.loopModeConfig.numRepetitions > this.maxAllowedRepetitions ||
      this.testImpl.loopModeConfig.timeBetweenRepetitions <= 0 ||
      this.testImpl.loopModeConfig.timeBetweenRepetitions >= this.maxTimeBetweenRepetitions
    ) {
      this.showInvalidInputWarning = true;
      return;
    }

    if (this.testImpl.loopModeConfig.startTime) {
      const startDate = new Date(this.testImpl.loopModeConfig.startTime);

      console.log("isNan: " + startDate.getTime() + " -> "  + isNaN(startDate.getTime()) + "");

      if ((isNaN(startDate.getTime())) || (Date.now() - startDate.getTime() > 0)) {
        this.showInvalidInputWarning = true;
        return;
      }
    }

    this.showInvalidInputWarning = false;

    if (this.testImpl.loopModeConfig.startTime) {
      this.startTimerForNextTest();
    }
    else {
      super.requestStart();
    }
  }

  public startTimerForNextTest() {
    this.testImpl.alreadyStarted = true;

    if (this.testImpl.curRepetitions >= this.testImpl.loopModeConfig.numRepetitions) {
      console.log('REACHED END OF LOOP MODE!');
      return;
    }

    console.log('START TIMER FOR NEXT TEST!');

    let timeLeft = this.testImpl.loopModeConfig.timeBetweenRepetitions * 60;

    if (this.testImpl.curRepetitions <= 0 && this.testImpl.loopModeConfig.startTime) {
      const millisLeft = new Date(timeLeft = this.testImpl.loopModeConfig.startTime).getTime() - Date.now();
      this.testImpl.loopModeConfig.startTime = null;
      timeLeft = millisLeft / 1000;
    }

    this.testImpl.timeLeftString = this.toHHMMSS(timeLeft);
    this.timerInterval = setInterval(() => {
      if (timeLeft > 1) {
        timeLeft--;
      } else {
        clearInterval(this.timerInterval);
        timeLeft = 0;
        this.requestStart();
      }

      this.testImpl.timeLeftString = this.toHHMMSS(timeLeft);
    }, 1000);
  }

  private toHHMMSS(timeLeft): string {
    var sec_num = parseInt(timeLeft, 10);
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor(sec_num / 60) % 60;
    var seconds = sec_num % 60;

    return [hours,minutes,seconds]
        .map(v => v < 10 ? "0" + v : v)
        .filter((v,i) => v !== "00" || i > 0)
        .join(":");
  }
}
