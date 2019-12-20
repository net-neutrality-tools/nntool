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
import { StatisticApiService } from './statistic-api.service';
import { DateParseService } from './date-parse.service';

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
  StatisticApiService,
  TestService,
  TestSettingsService,
  LocationService,
  DateParseService,
  Guard,
  WINDOW_REF_PROVIDER,
  WINDOW_PROVIDER
];

@NgModule({
  imports: [SharedModule],
  providers: [...Services]
})
export class ServicesModule {}
