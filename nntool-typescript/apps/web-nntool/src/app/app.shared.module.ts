import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerModule, NgxLoggerLevel } from 'ngx-logger';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { DeviceDetectorModule } from 'ngx-device-detector';
import { AppComponent } from './app.component';
import { TEST_DECLARATIONS, TEST_PROVIDERS } from './test/test.module';
import { TestSeriesComponent } from './testing/test-series.component';
import { PortBlockingTestImplementation } from './testing/tests-implementation/port-blocking/port-blocking-test-implementation';
import { SpeedTestImplementation } from './testing/tests-implementation/speed/speed-test-implementation';
import { PortBlockingTestBarComponent } from './testing/tests/port-blocking-test-bar';
import { SpeedTestGaugeComponent } from './testing/tests/speed-test-gauge';

// app
import { CoreModule } from './@core/core.module';
import { PagesModule } from './pages/pages.module';
import { SharedModule } from './shared/shared.module';
import { environment } from '../environments/environment';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/');
}

const MAIN_DECLARATIONS = [AppComponent];

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
    HttpClientModule,
    DeviceDetectorModule.forRoot(),
    CoreModule,
    PagesModule,
    SharedModule,
    Ng2SmartTableModule,
    LeafletModule.forRoot()
  ],
  declarations: [
    ...MAIN_DECLARATIONS,
    ...TEST_DECLARATIONS,
    SpeedTestGaugeComponent,
    PortBlockingTestBarComponent,
    TestSeriesComponent
  ],
  providers: [...TEST_PROVIDERS, SpeedTestImplementation, PortBlockingTestImplementation]
  //bootstrap: [AppComponent]
})
export class AppSharedModule {}
