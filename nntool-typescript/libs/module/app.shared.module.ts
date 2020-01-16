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

import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerModule, NgxLoggerLevel } from 'ngx-logger';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { DeviceDetectorModule } from 'ngx-device-detector';
import { TEST_DECLARATIONS, TEST_PROVIDERS } from '../test/test.module';
import { TestSeriesComponent } from '../testing/test-series.component';
import { PortBlockingTestImplementation } from '../testing/tests-implementation/port-blocking/port-blocking-test-implementation';
import { SpeedTestImplementation } from '../testing/tests-implementation/speed/speed-test-implementation';
import { PortBlockingTestBarComponent } from '../testing/tests/port-blocking-test-bar';
import { SpeedTestGaugeComponent } from '../testing/tests/speed-test-gauge';

import { CoreModule } from '../core/core.module';
import { PagesModule } from '../pages/pages.module';
import { SharedModule } from '../shared/shared.module';
import { environment } from '@env/environment';
import { LoopSettingsComponent } from '@nntool-typescript/testing/tests/loop-settings';
import { TracerouteTestBarComponent } from '@nntool-typescript/testing/tests/traceroute-test-bar';
import { TracerouteTestImplementation } from '@nntool-typescript/testing/tests-implementation/traceroute/traceroute-test-implementation';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/');
}

const MAIN_DECLARATIONS = [];

@NgModule({
  imports: [
    LoggerModule.forRoot({
      level: NgxLoggerLevel.DEBUG,
      serverLogLevel: NgxLoggerLevel.OFF,
      enableSourceMaps: !environment.production
    }),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    DeviceDetectorModule.forRoot(),
    Ng2SmartTableModule,
    LeafletModule.forRoot(),
    CoreModule,
    PagesModule,
    SharedModule
  ],
  declarations: [
    ...MAIN_DECLARATIONS,
    ...TEST_DECLARATIONS,
    SpeedTestGaugeComponent,
    PortBlockingTestBarComponent,
    TracerouteTestBarComponent,
    LoopSettingsComponent,
    TestSeriesComponent
  ],
  providers: [...TEST_PROVIDERS, SpeedTestImplementation, PortBlockingTestImplementation, TracerouteTestImplementation],
  exports: [TranslateModule, BrowserModule, BrowserAnimationsModule, CoreModule]
})
export class AppSharedModule {}
