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

import { AfterViewInit, Component, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable } from 'rxjs';
import { first } from 'rxjs/operators';
import { Test } from '../../test.component';
import { TestConfig } from '../../tests-implementation/test-config';
import { TestImplementation } from '../../tests-implementation/test-implementation';
import { TestState } from '../../tests-implementation/test-state';
import { BaseMeasurementGauge } from './existing-gauge-ui/base.gauge.ui';
import { MeasurementGauge } from './existing-gauge-ui/gauge.ui';
import { GaugeUIState } from './gauge-ui-state';
import { WebsiteSettings } from '../../../core/models/settings/settings.interface';
import { ConfigService } from '../../../core/services/config.service';
import { WINDOW } from '../../../core/services/window.service';

/*@Component({
  templateUrl: './gauge-ui.template.html'
})*/
export abstract class GaugeUIComponent<
  T extends TestImplementation<TC, TS>,
  TC extends TestConfig,
  TS extends TestState
> extends Test<GaugeUIState, T, TC, TS> implements AfterViewInit {
  public active: boolean;

  protected testGauge: BaseMeasurementGauge;
  private renderingConfig: WebsiteSettings;

  protected constructor(
    protected logger: NGXLogger,
    testImplementation: T,
    configService: ConfigService,
    protected translateService: TranslateService,
    @Inject(WINDOW) private window: Window
  ) {
    super(testImplementation);
    this.state.subscribe(this.handleState);
    this.renderingConfig = configService.getConfig();
  }

  public setActive = (active: boolean) => {
    this.active = active;
    if (this.active && this.testGauge) {
      setTimeout(() => {
        this.testGauge.resizeEvent();
      }, 0);
    }
  };

  public ngAfterViewInit() {
    this.testGauge = this.getGauge();
    this.testGauge.draw();
  }

  private handleState = (state: GaugeUIState) => {
    if (this.testGauge) {
      this.testGauge.onStateChange(state);
    }
  };

  private getGauge(): any {
    const hasQos: boolean =
      this.renderingConfig.nettest && this.renderingConfig.nettest.tests && this.renderingConfig.nettest.tests.qos;

    const translations: { [key: string]: any } = {
      SPEED_MBPS: 'Mbps',
      DURATION_MS: 'ms',
      INNER_TEXTS: ['0MBPS', '1MBPS', '10MBPS', '100MBPS', '1GBPS'],
      OUTER_TEXTS: ['INIT', 'PING', 'DOWN', 'UP']
    };
    if (hasQos) {
      translations.OUTER_TEXTS.push('QOS');
    }
    let gaugeColors: { [key: string]: string } = {
      baseColor: '#EEEEEE',
      valueColor: '#878787',
      progressColor: '#911232',
      fontColor: '#FFFFFF'
    };
    let font: string = null;
    if (this.renderingConfig.colors.gauge) {
      const gc: { [key: string]: string } = this.renderingConfig.colors.gauge;
      gaugeColors = {
        baseColor: gc.arc_background,
        valueColor: gc.arc_inner,
        progressColor: gc.arc_outer,
        fontColor: gc.font
      };

      if (gc.fontName) {
        font = gc.fontName;
      }
    }
    const gauge: any = new MeasurementGauge(
      this.logger,
      document.getElementById('nettest-gauge') as HTMLCanvasElement,
      document.getElementById('nettest-gauge-2') as HTMLCanvasElement,
      document.getElementById('nettest-state'),
      document.getElementById('nettest-ping'),
      document.getElementById('nettest-up'),
      document.getElementById('nettest-down'),
      document.getElementById('nettest-position'),
      document.getElementById('nettest-provider'),
      document.getElementById('nettest-device'),
      document.getElementById('nettest-technology'),
      document.getElementById('nettest-server'),
      this.window,
      translations,
      gaugeColors,
      font,
      hasQos
    );

    let firstRun = true;
    const translate: any = () => {
      const obs: Array<Observable<string>> = [];
      for (const key in translations) {
        if (key === 'INNER_TEXTS' || key === 'OUTER_TEXTS') {
          for (const gaugeKey of translations[key]) {
            obs.push(this.translateService.get('NETTEST.GAUGE.' + gaugeKey));
          }
        } else {
          obs.push(this.translateService.get('WEBSITE.UNITS.' + key));
        }
      }

      forkJoin(obs)
        .pipe(first())
        .subscribe((data: any) => {
          let i = 0;
          const res: any = {};

          for (const key in translations) {
            if (!translations.hasOwnProperty(key)) {
              continue;
            }
            if (i >= data.length) {
              break;
            }
            if (key === 'INNER_TEXTS' || key === 'OUTER_TEXTS') {
              res[key] = [];
              for (const gaugeKey of translations[key]) {
                res[key].push(data[i]);
                i++;
              }
            } else {
              res[key] = data[i];
              i++;
            }
          }

          if (res) {
            gauge.translations = res;
            gauge.draw();
          }
        });
      if (firstRun) {
        this.translateService.onLangChange.subscribe(translate);
        firstRun = false;
      }
    };
    translate();
    return gauge;
  }
}
