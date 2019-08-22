import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// import {NvD3Component} from 'ng2-nvd3';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NvD3Module } from 'ng2-nvd3';

import { DeviceDetectorModule } from 'ngx-device-detector';
import { NotFoundComponent } from './404/notfound.component';
import { AboutComponent } from './about/about.component';
import { ADocService } from './adoc/adoc.service';
import { AppComponent } from './app.component';
import { routing } from './app.routes';
import { DocuComponent } from './docu/docu.component';
import { FooterComponent } from './footer/footer.component';
import { HelpComponent } from './help/help.component';
import { HistoryComponent } from './history/history.component';
import { HistoryDeletionComponent } from './history/history.deletion.component';
import { HistoryViewComponent } from './history/history.view.component';
import { HistoryDetailViewComponent } from './history/view-detail.component';
import { HomeComponent } from './home/home.component';
import { MapComponent } from './map/map.component';
import { MeasurementClassifiedValueComponent } from './measurement/classified.value.component';
import { MeasurementFormattedValueComponent } from './measurement/formatted.value.component';
import { MeasurementGroupedComponent } from './measurement/grouped.component';
import { MeasurementService } from './measurement/measurement.service';
import { SpeedGraphComponent } from './measurement/speed.graph.component';
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
import { ResultListComponent } from './result-list/result_list.component';
import { OpentestDetailViewComponent } from './result/view-detail.component';
import { OpentestViewComponent } from './result/view.component';
import { AppService } from './services/app.service';
import { ColorService } from './services/color.service';
import { ConfigService } from './services/config.service';
import { Guard } from './services/guard.service';
import { LocationService } from './services/location.service';
import { LoggerService } from './services/log.service';
import { MapService } from './services/map.service';
import { RequestsService } from './services/requests.service';
import { BrowserStorageService } from './services/storage.service';
import { TestSettingsService } from './services/test/test-settings.service';
import { TestService } from './services/test/test.service';
import { UserService } from './services/user.service';
import { WINDOW_PROVIDER, WINDOW_REF_PROVIDER } from './services/window.service';
import { StatisticsComponent } from './statistics/statistics.component';
import { TCComponent } from './tc/tc.component';
import { TEST_DECLARATIONS, TEST_PROVIDERS } from './test/test.module';
import { TestSeriesComponent } from './testing/test-series.component';
import { PortBlockingTestImplementation } from './testing/tests-implementation/port-blocking/port-blocking-test-implementation';
import { SpeedTestImplementation } from './testing/tests-implementation/speed/speed-test-implementation';
import { PortBlockingTestBarComponent } from './testing/tests/port-blocking-test-bar';
import { SpeedTestGaugeComponent } from './testing/tests/speed-test-gauge';
import { SettingsComponent } from './user/settings.component';

// app
import { CoreModule } from './core/core.module';
import { SharedModule } from './features/shared/shared.module';
import { RequestInfoService } from './services/request-info.service';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/');
}

const MAIN_DECLARATIONS = [
  AppComponent,
  // NvD3Component,
  MapComponent,
  StatisticsComponent,
  ResultListComponent,
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
  HistoryDetailViewComponent,
  HistoryDeletionComponent,
  HistoryViewComponent,
  QoSResultComponent,
  MeasurementClassifiedValueComponent,
  MeasurementFormattedValueComponent,
  TimestampLocalDateFormatPipe,
  MeasurementGroupedComponent,
  OpentestViewComponent,
  OpentestDetailViewComponent,
  SpeedGraphComponent,
  SettingsComponent,
  DocuComponent,
  FooterComponent,
  RoundPipe
];

@NgModule({
  imports: [
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
    SharedModule,
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
    LoggerService,
    BrowserStorageService,
    UserService,
    MeasurementService,
    AppService,
    MapService,
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
