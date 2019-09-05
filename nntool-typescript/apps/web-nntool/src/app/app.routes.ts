import { ModuleWithProviders } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Guard } from './services/guard.service';
import { NetTestComponent, TestGuard } from './test/test.component';
import { HomeComponent } from './pages/home/home.component';
import { HistoryComponent } from './pages/history/history.component';
import { HistoryViewComponent } from './pages/history/history.view.component';
import { OpenDataResultTableComponent } from './pages/opendata/open-data-result-table.component';
import { OpenDataResultComponent } from './pages/opendata/open-data-result.component';
import { MapComponent } from './pages/map/map.component';
import { StatisticsComponent } from './pages/statistics/statistics.component';
import { OpendataComponent } from './pages/opendata/opendata.component';
import { TcComponent } from './pages/adoc/tc.component';
import { HelpComponent } from './pages/adoc/help.component';
import { SettingsComponent } from './pages/user/settings.component';
import { NotFoundComponent } from './pages/adoc/notfound.component';

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

export const routing: ModuleWithProviders = RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' });
