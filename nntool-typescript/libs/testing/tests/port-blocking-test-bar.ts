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

import { Component, Inject } from '@angular/core';
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

@Component({
  // needs to be mentioned here, but also mentioned in gauge-ui.ts for reference
  templateUrl: '../tests-ui/bar/bar-ui.template.html',
  selector: 'app-port-blocking-test-bar'
})
export class PortBlockingTestBarComponent extends BarUIComponent<
  PortBlockingTestImplementation,
  PortBlockingTestConfig,
  PortBlockingTestState
> {
  // TODO: rethink DI in this use case, testImplementation should not be one instance, if there were more than one test at once
  // TODO: Remove this constructor when DI on generic type figured out
  constructor(
    testImplementation: PortBlockingTestImplementation,
    configService: ConfigService,
    translateService: TranslateService,
    @Inject(WINDOW) window: Window
  ) {
    super(testImplementation, configService, translateService, window);
  }

  protected testStateToUIState = (state: PortBlockingTestState): BarUIState => {
    const barUIState: BarUIState = new BarUIState();
    barUIState.types = [];
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
    );
    return barUIState;
  };
}
