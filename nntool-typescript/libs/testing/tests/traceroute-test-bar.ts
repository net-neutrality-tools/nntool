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

import { Component, Inject, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { BarUIComponent } from '../tests-ui/bar/bar-ui';
import { BarUIState } from '../tests-ui/bar/bar-ui-state';
import { BarUIShowableTestTypeEnum } from '../tests-ui/bar/enums/bar-ui-showable-test-type.enum';
import { ConfigService } from '../../core/services/config.service';
import { WINDOW } from '../../core/services/window.service';
import { TracerouteTestImplementation } from '../tests-implementation/traceroute/traceroute-test-implementation';
import { SpeedTestConfig } from '../tests-implementation/speed/speed-test-config';
import { TracerouteTestState } from '../tests-implementation/traceroute/traceroute-test-state';

@Component({
    // needs to be mentioned here, but also mentioned in gauge-ui.ts for reference
    templateUrl: '../tests-ui/bar/bar-ui.template.html',
    selector: 'app-traceroute-test-bar'
})
export class TracerouteTestBarComponent extends BarUIComponent<
    TracerouteTestImplementation,
    SpeedTestConfig,
    TracerouteTestState
>  implements OnDestroy {
    // TODO: rethink DI in this use case, testImplementation should not be one instance, if there were more than one test at once
    // TODO: Remove this constructor when DI on generic type figured out
    constructor(
        testImplementation: TracerouteTestImplementation,
        configService: ConfigService,
        translateService: TranslateService,
        @Inject(WINDOW) window: Window
    ) {
        super(testImplementation, configService, translateService, window);
        this.testImpl = testImplementation;
    }

    private testImpl: TracerouteTestImplementation;

    protected testStateToUIState = (state: TracerouteTestState): BarUIState => {
        const barUIState: BarUIState = new BarUIState();
        barUIState.types = [];

        if (state.progress) {
            
            barUIState.types.push({
                progress: state.progress,
                key: BarUIShowableTestTypeEnum.TRACEROUTE
            });
        }
        
        return barUIState;
    };

    ngOnDestroy() {
        this.testImpl.onDestroy();
    }
}
