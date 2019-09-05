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
import { NotFoundComponent } from './adoc/notfound.component';
import { AboutComponent } from './adoc/about.component';
import { TCComponent } from './adoc/tc.component';
import { DocuComponent } from './adoc/docu.component';
import { HelpComponent } from './adoc/help.component';
import { ADocService } from './adoc/adoc.service';
import { AppComponent } from './app.component';
import { routing } from './app.routes';
import { FooterComponent } from './footer/footer.component';
import { HistoryComponent } from './history/history.component';
import { HistoryDeletionComponent } from './history/history.deletion.component';
import { HistoryViewComponent } from './history/history.view.component';
import { HomeComponent } from './home/home.component';
import { MapComponent } from './map/map.component';
import { MeasurementClassifiedValueComponent } from './measurement/classified.value.component';
import { MeasurementFormattedValueComponent } from './measurement/formatted.value.component';
import { MeasurementService } from './measurement/measurement.service';
import { OpendataComponent } from './opendata/opendata.component';
import { ClassificationFormatPipe } from './pipes/classification.format.pipe';
import { FixedFormatPipe } from './pipes/fixed.format.pipe';
import { InsecureSanitizeHtml } from './pipes/insecure.sanitizer.pipe';
import { IterateMapKeysPipe } from './pipes/iterateMapKeys.format.pipe';
import { LocationFormatPipe } from './pipes/location.format.pipe';
import { NumberFormatPipe } from './pipes/number.format.pipe';
import { PingFormatPipe } from './pipes/ping.format.pipe';
import { RoundPipe } from './pipes/round.pipe';
import { SpeedFormatPipe } from './pipes/speed.format.pipe';
import { TimestampLocalDateFormatPipe } from './pipes/timestamp.date.format.pipe';
import { UTCLocalDateFormatPipe } from './pipes/utc.date.format.pipe';
import { QoSResultComponent } from './qos-result/qos-result.component';
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
import { StatisticsComponent } from './statistics/statistics.component';
import { TEST_DECLARATIONS, TEST_PROVIDERS } from './test/test.module';
import { TestSeriesComponent } from './testing/test-series.component';
import { PortBlockingTestImplementation } from './testing/tests-implementation/port-blocking/port-blocking-test-implementation';
import { SpeedTestImplementation } from './testing/tests-implementation/speed/speed-test-implementation';
import { PortBlockingTestBarComponent } from './testing/tests/port-blocking-test-bar';
import { SpeedTestGaugeComponent } from './testing/tests/speed-test-gauge';
import { SettingsComponent } from './user/settings.component';
import { ComponentsModule } from './components/components.module';

// app
import { CoreModule } from './core/core.module';
import { SharedModule } from './features/shared/shared.module';
import { RequestInfoService } from './services/request-info.service';
import { OpenDataResultTableComponent } from './opendata/open-data-result-table.component';
import { SearchApiService } from './services/search-api.service';
import { ResultApiService } from './services/result-api.service';
import { OpenDataResultComponent } from './opendata/open-data-result.component';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/');
}

const MAIN_DECLARATIONS = [
  AppComponent,
  // NvD3Component,
  MapComponent,
  StatisticsComponent,
  OpenDataResultTableComponent,
  OpenDataResultComponent,
  HelpComponent,
  AboutComponent,
  HistoryComponent,
  NumberFormatPipe,
  SpeedFormatPipe,
  PingFormatPipe,
  NotFoundComponent,
  LocationFormatPipe,
  ClassificationFormatPipe,
  IterateMapKeysPipe,
  FixedFormatPipe,
  OpendataComponent,
  HomeComponent,
  TCComponent,
  InsecureSanitizeHtml,
  UTCLocalDateFormatPipe,
  HistoryDeletionComponent,
  HistoryViewComponent,
  QoSResultComponent,
  MeasurementClassifiedValueComponent,
  MeasurementFormattedValueComponent,
  TimestampLocalDateFormatPipe,
  SettingsComponent,
  DocuComponent,
  FooterComponent,
  RoundPipe
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
    ComponentsModule,
    CoreModule,
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
export class AppModule {}
