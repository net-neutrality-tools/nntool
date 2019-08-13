import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';

//import {NvD3Component} from 'ng2-nvd3';
import {NvD3Module} from 'ng2-nvd3';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';

import {AppComponent} from './app.component';
import {MapComponent} from './map/map.component';
import {ConfigService} from './services/config.service';
import {RequestsService} from './services/requests.service';
import {StatisticsComponent} from './statistics/statistics.component';
import {routing} from './app.routes';
import {HelpComponent} from './help/help.component';
import {AboutComponent} from './about/about.component';
import {HistoryComponent} from './history/history.component';
import {Guard} from './services/guard.service';
import {NumberFormatPipe} from './pipes/number.format.pipe';
import {SpeedFormatPipe} from './pipes/speed.format.pipe';
import {PingFormatPipe} from './pipes/ping.format.pipe';
import {ADocService} from './adoc/adoc.service';
import {NotFoundComponent} from './404/notfound.component';
import {LocationFormatPipe} from './pipes/location.format.pipe';
import {ClassificationFormatPipe} from './pipes/classification.format.pipe';
import {IterateMapKeysPipe} from './pipes/iterateMapKeys.format.pipe';
import {ResultListComponent} from './result-list/result_list.component';
import {FixedFormatPipe} from './pipes/fixed.format.pipe';
import {ColorService} from './services/color.service';
import {OpendataComponent} from './opendata/opendata.component';
import {TCComponent} from './tc/tc.component';
import {HomeComponent} from './home/home.component';
import {InsecureSanitizeHtml} from './pipes/insecure.sanitizer.pipe';
import {LoggerService} from './services/log.service';
import {UTCLocalDateFormatPipe} from './pipes/utc.date.format.pipe';
import {HistoryDetailViewComponent} from './history/view-detail.component';
import {HistoryViewComponent} from './history/view.component';
import {MeasurementClassifiedValueComponent} from './measurement/classified.value.component';
import {MeasurementFormattedValueComponent} from './measurement/formatted.value.component';
import {TimestampLocalDateFormatPipe} from './pipes/timestamp.date.format.pipe';
import {MeasurementGroupedComponent} from './measurement/grouped.component';
import {OpentestViewComponent} from './result/view.component';
import {OpentestDetailViewComponent} from './result/view-detail.component';
import {SpeedGraphComponent} from './measurement/speed.graph.component';
import {SettingsComponent} from './user/settings.component';
import {BrowserStorageService} from './services/storage.service';
import {UserService} from './services/user.service';
import {MeasurementService} from './measurement/measurement.service';
import {AppService} from './services/app.service';
import {DocuComponent} from './docu/docu.component';
import {FooterComponent} from './footer/footer.component';
import {TEST_DECLARATIONS, TEST_PROVIDERS} from './test/test.module';
import {RoundPipe} from './pipes/round.pipe';
import {MapService} from './services/map.service';
import {SpeedTestImplementation} from './testing/tests-implementation/speed/speed-test-implementation';
import {SpeedTestGaugeComponent} from './testing/tests/speed-test-gauge';
import {PortBlockingTestBarComponent} from './testing/tests/port-blocking-test-bar';
import {PortBlockingTestImplementation} from './testing/tests-implementation/port-blocking/port-blocking-test-implementation';
import {TestSeriesComponent} from './testing/test-series.component';
import {TestService} from './services/test/test.service';
import {TestSettingsService} from './services/test/test-settings.service';
import {WINDOW_PROVIDER, WINDOW_REF_PROVIDER} from './services/window.service';
import {LocationService} from './services/location.service';
import {DeviceDetectorModule} from 'ngx-device-detector';

// app
import { CoreModule } from './core/core.module';
import { SharedModule } from './features/shared/shared.module';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/');
}

const MAIN_DECLARATIONS = [
    AppComponent,
    //NvD3Component,
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
    HistoryViewComponent,
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
    RoundPipe,
];


@NgModule({
    imports: [
        BrowserModule,
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
        CoreModule, SharedModule,
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
export class AppModule {
}
