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
import { CoreModule } from '@nntool-typescript/core';
import { ComponentsModule } from '../core/components/components.module';
import { StatisticsComponent } from './statistics/statistics.component';
import { SharedModule } from '../shared/shared.module';
import { MapComponent } from './map/map.component';
import { SettingsComponent } from './user/settings.component';
import { HistoryComponent } from './history/history.component';
import { HistoryViewComponent } from './history/history.view.component';
import { OpendataComponent } from './opendata/opendata.component';
import { OpenDataResultTableComponent } from './opendata/open-data-result-table.component';
import { OpenDataResultComponent } from './opendata/open-data-result.component';
import { TcComponent } from './adoc/tc.component';
import { HelpComponent } from './adoc/help.component';
import { AboutComponent } from './adoc/about.component';
import { NotFoundComponent } from './adoc/notfound.component';
import { HistoryDeletionComponent } from './history/history.deletion.component';
import { PipesModule } from '../core/pipes/pipes.module';
import { ADocComponent } from './adoc/adoc.component';
import { DocuComponent } from './adoc/docu.component';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

const COMPONENTS = [
  HistoryComponent,
  HistoryViewComponent,
  HistoryDeletionComponent,
  OpendataComponent,
  OpenDataResultTableComponent,
  OpenDataResultComponent,
  MapComponent,
  StatisticsComponent,
  SettingsComponent,
  ADocComponent,
  DocuComponent,
  TcComponent,
  HelpComponent,
  AboutComponent,
  NotFoundComponent
];

@NgModule({
  imports: [SharedModule, CoreModule, ComponentsModule, PipesModule, LeafletModule],
  exports: [...COMPONENTS],
  declarations: [...COMPONENTS],
  entryComponents: [...COMPONENTS]
})
export class PagesModule {}
