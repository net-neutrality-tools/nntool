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

import { Routes } from '@angular/router';
import { Guard } from '@nntool-typescript/core/services/guard.service';
import { NetTestComponent } from '@nntool-typescript/test/test.component';
import { TestGuard } from '@nntool-typescript/test/base_test.component';
import { HistoryComponent } from '@nntool-typescript/pages/history/history.component';
import { HistoryViewComponent } from '@nntool-typescript/pages/history/history.view.component';
import { OpenDataResultTableComponent } from '@nntool-typescript/pages/opendata/open-data-result-table.component';
import { OpenDataResultComponent } from '@nntool-typescript/pages/opendata/open-data-result.component';
import { MapComponent } from '@nntool-typescript/pages/map/map.component';
import { StatisticsComponent } from '@nntool-typescript/pages/statistics/statistics.component';
import { OpendataComponent } from '@nntool-typescript/pages/opendata/opendata.component';
import { TcComponent } from '@nntool-typescript/pages/adoc/tc.component';
import { HelpComponent } from '@nntool-typescript/pages/adoc/help.component';
import { SettingsComponent } from '@nntool-typescript/pages/user/settings.component';
import { NotFoundComponent } from '@nntool-typescript/pages/adoc/notfound.component';
import { HomeComponent } from './home/home.component';

export const routes: Routes = [
  {
    path: '',
    canActivate: [Guard],
    canActivateChild: [Guard],
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: 'home',
        component: HomeComponent
      },
      {
        path: 'nettest',
        component: NetTestComponent,
        canDeactivate: [TestGuard]
      },
      {
        path: 'history',
        children: [
          {
            path: '',
            component: HistoryComponent,
            pathMatch: 'full'
          },
          {
            path: ':uuid',
            children: [
              {
                path: '',
                component: HistoryViewComponent,
                pathMatch: 'full'
              }
            ]
          }
        ]
      },
      {
        path: 'open-data-results',
        children: [
          {
            path: '',
            component: OpenDataResultTableComponent,
            pathMatch: 'full'
          },
          {
            path: ':openDataUuid',
            children: [
              {
                path: '',
                component: OpenDataResultComponent,
                pathMatch: 'full'
              }
            ]
          }
        ]
      },
      {
        path: 'map',
        component: MapComponent
      },
      {
        path: 'statistics',
        component: StatisticsComponent
      },
      {
        path: 'opendata',
        component: OpendataComponent
      },
      {
        path: 'tc',
        component: TcComponent
      },
      {
        path: 'help',
        component: HelpComponent
      },
      /*{
        path: 'about',
        component: AboutComponent
      },*/
      /*{
        path: 'docu',
        component: DocuComponent
      },*/
      {
        path: 'settings',
        component: SettingsComponent
      }
    ]
  },
  {
    path: '404',
    component: NotFoundComponent
  },
  {
    path: '**',
    redirectTo: '404'
  }
];
