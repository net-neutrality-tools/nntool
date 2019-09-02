import { ModuleWithProviders } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { NotFoundComponent } from './404/notfound.component';
import { AboutComponent } from './about/about.component';
import { DocuComponent } from './docu/docu.component';
import { HelpComponent } from './help/help.component';
import { HistoryComponent } from './history/history.component';
import { HistoryViewComponent } from './history/history.view.component';
import { HistoryDetailViewComponent } from './history/view-detail.component';
import { HomeComponent } from './home/home.component';
import { MapComponent } from './map/map.component';
import { OpendataComponent } from './opendata/opendata.component';
import { OpentestViewComponent } from './result/view.component';
import { Guard } from './services/guard.service';
import { StatisticsComponent } from './statistics/statistics.component';
import { TCComponent } from './tc/tc.component';
import { NetTestComponent, TestGuard } from './test/test.component';
import { SettingsComponent } from './user/settings.component';
import { OpenDataResultListComponent } from './opendata/open-data-result-list.component';

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
        path: 'tc',
        component: TCComponent
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
        path: 'map',
        component: MapComponent
      },
      {
        path: 'open-data-results',
        children: [
          {
            path: '',
            component: OpenDataResultListComponent,
            pathMatch: 'full'
          },
          {
            path: ':openDataUuid',
            children: [
              {
                path: '',
                component: OpentestViewComponent /*OpenDataResultComponent*/,
                pathMatch: 'full'
              }
              /*{
                path: 'details',
                component: OpenDataResultDetailComponent
              }*/
            ]
          }
        ]
      },
      /*{
        path: 'testresults',
        children: [
          {
            path: '',
            component: ResultListComponent,
            pathMatch: 'full'
          },
          {
            path: ':uuid',
            children: [
              {
                path: '',
                component: OpentestViewComponent,
                pathMatch: 'full'
              },
              {
                path: 'details',
                component: OpentestDetailViewComponent
              }
            ]
          }
        ]
      },*/
      {
        path: 'nettest',
        component: NetTestComponent,
        canDeactivate: [TestGuard]
      },
      {
        path: 'about',
        component: AboutComponent
      },
      {
        path: 'docu',
        component: DocuComponent
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
              },
              {
                path: 'details',
                component: HistoryDetailViewComponent
              }
            ]
          }
        ]
      },
      {
        path: 'help',
        component: HelpComponent
      },
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
