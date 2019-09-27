import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { ConfigService } from './config.service';
import { RequestsService } from './requests.service';
import { RequestInfoService } from './request-info.service';
import { ADocService } from '../../pages/adoc/adoc.service';
import { ColorService } from './color.service';
import { BrowserStorageService } from './storage.service';
import { UserService } from './user.service';
import { AppService } from './app.service';
import { MapService } from './map.service';
import { ResultApiService } from './result-api.service';
import { SearchApiService } from './search-api.service';
import { TestService } from './test/test.service';
import { TestSettingsService } from './test/test-settings.service';
import { LocationService } from './location.service';
import { Guard } from './guard.service';
import { WINDOW_REF_PROVIDER, WINDOW_PROVIDER } from './window.service';

const Services = [
  ConfigService,
  RequestsService,
  RequestInfoService,
  ADocService,
  ColorService,
  BrowserStorageService,
  UserService,
  AppService,
  MapService,
  ResultApiService,
  SearchApiService,
  TestService,
  TestSettingsService,
  LocationService,
  Guard,
  WINDOW_REF_PROVIDER,
  WINDOW_PROVIDER,
];

@NgModule({
  imports: [SharedModule,],
  providers: [...Services]
})
export class ServicesModule { }
