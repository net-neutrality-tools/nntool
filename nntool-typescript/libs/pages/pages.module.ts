import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
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
  HomeComponent,
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
