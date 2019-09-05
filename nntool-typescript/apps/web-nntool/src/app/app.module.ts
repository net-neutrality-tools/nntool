import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerModule, NgxLoggerLevel } from 'ngx-logger';
import { Ng2SmartTableModule } from 'ng2-smart-table';

// import {NvD3Component} from 'ng2-nvd3';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NvD3Module } from 'ng2-nvd3';

import { DeviceDetectorModule } from 'ngx-device-detector';
import { AppComponent } from './app.component';
import { routing } from './app.routes';
import { FooterComponent } from './footer/footer.component';
import { MeasurementClassifiedValueComponent } from './measurement/classified.value.component';
import { MeasurementFormattedValueComponent } from './measurement/formatted.value.component';
import { MeasurementService } from './measurement/measurement.service';
import { AppService } from './services/app.service';
import { ColorService } from './services/color.service';
import { ConfigService } from './services/config.service';
import { Guard } from './services/guard.service';
import { LocationService } from './services/location.service';
import { MapService } from './services/map.service';
import { RequestsService } from './services/requests.service';
import { BrowserStorageService } from './services/storage.service';
import { TestSettingsService } from './services/test/test-settings.service';
import { TestService } from './services/test/test.service';
import { UserService } from './services/user.service';
import { WINDOW_PROVIDER, WINDOW_REF_PROVIDER } from './services/window.service';
import { TEST_DECLARATIONS, TEST_PROVIDERS } from './test/test.module';
import { TestSeriesComponent } from './testing/test-series.component';
import { PortBlockingTestImplementation } from './testing/tests-implementation/port-blocking/port-blocking-test-implementation';
import { SpeedTestImplementation } from './testing/tests-implementation/speed/speed-test-implementation';
import { PortBlockingTestBarComponent } from './testing/tests/port-blocking-test-bar';
import { SpeedTestGaugeComponent } from './testing/tests/speed-test-gauge';

// app
import { RequestInfoService } from './services/request-info.service';
import { SearchApiService } from './services/search-api.service';
import { ResultApiService } from './services/result-api.service';
import { CoreModule } from './@core/core.module';
import { PagesModule } from './pages/pages.module';
import { SharedModule } from './shared/shared.module';
import { ADocService } from './pages/adoc/adoc.service';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/');
}

const MAIN_DECLARATIONS = [
  AppComponent,
  // NvD3Component,
  MeasurementClassifiedValueComponent,
  MeasurementFormattedValueComponent,
  FooterComponent
];

@NgModule({
  imports: [
    LoggerModule.forRoot({
      level: NgxLoggerLevel.DEBUG,
      serverLogLevel: NgxLoggerLevel.OFF,
      enableSourceMaps: true
    }),
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    routing,
    DeviceDetectorModule.forRoot(),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    CoreModule,
    PagesModule,
    SharedModule,
    Ng2SmartTableModule,
    NvD3Module
  ],
  declarations: [
    ...MAIN_DECLARATIONS,
    ...TEST_DECLARATIONS,
    SpeedTestGaugeComponent,
    PortBlockingTestBarComponent,
    TestSeriesComponent
  ],
  providers: [
    ConfigService,
    RequestsService,
    RequestInfoService,
    Guard,
    ADocService,
    ColorService,
    BrowserStorageService,
    UserService,
    MeasurementService,
    AppService,
    MapService,
    ResultApiService,
    SearchApiService,
    ...TEST_PROVIDERS,
    SpeedTestImplementation,
    PortBlockingTestImplementation,
    TestService,
    TestSettingsService,
    WINDOW_REF_PROVIDER,
    WINDOW_PROVIDER,
    LocationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
