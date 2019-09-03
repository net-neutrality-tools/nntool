import { ModuleWithProviders } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { NotFoundComponent } from './adoc/notfound.component';
import { AboutComponent } from './adoc/about.component';
import { DocuComponent } from './adoc/docu.component';
import { HelpComponent } from './adoc/help.component';
import { TCComponent } from './adoc/tc.component';

import { HistoryComponent } from './history/history.component';
import { HistoryViewComponent } from './history/history.view.component';
import { HomeComponent } from './home/home.component';
import { MapComponent } from './map/map.component';
import { OpendataComponent } from './opendata/opendata.component';
import { Guard } from './services/guard.service';
import { StatisticsComponent } from './statistics/statistics.component';
import { NetTestComponent, TestGuard } from './test/test.component';
import { SettingsComponent } from './user/settings.component';
import { OpenDataResultTableComponent } from './opendata/open-data-result-table.component';

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
          }
          /*{
            path: ':openDataUuid',
            children: [
              {
                path: '',
                component: OpenDataResultComponent,
                pathMatch: 'full'
              }
            ]
          }*/
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
        component: TCComponent
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

export const routing: ModuleWithProviders = RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' });
