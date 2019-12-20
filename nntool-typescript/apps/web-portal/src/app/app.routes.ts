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

import { OpenDataResultTableComponent } from 'libs/pages/opendata/open-data-result-table.component';
import { OpenDataResultComponent } from 'libs/pages/opendata/open-data-result.component';
import { MapComponent } from 'libs/pages/map/map.component';
import { StatisticsComponent } from 'libs/pages/statistics/statistics.component';
import { OpendataComponent } from 'libs/pages/opendata/opendata.component';
import { HelpComponent } from 'libs/pages/adoc/help.component';
import { NotFoundComponent } from 'libs/pages/adoc/notfound.component';
import { Guard } from 'libs/core/services/guard.service';
import { PortalHomeComponent } from './portal-home/portal-home.component';

export const routes: Routes = [
  {
    path: '',
    canActivate: [Guard],
    canActivateChild: [Guard],
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: 'home',
        component: PortalHomeComponent
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
        path: 'help',
        component: HelpComponent
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
