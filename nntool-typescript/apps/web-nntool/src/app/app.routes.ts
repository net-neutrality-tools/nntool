import {Routes, RouterModule} from '@angular/router';
import {ModuleWithProviders} from '@angular/core';

import {StatisticsComponent} from './statistics/statistics.component';
import {MapComponent} from './map/map.component';
import {NetTestComponent, TestGuard} from './test/test.component';
import {AboutComponent} from './about/about.component';
import {HistoryComponent} from './history/history.component';
import {HelpComponent} from './help/help.component';
import {Guard} from './services/guard.service';
import {NotFoundComponent} from './404/notfound.component';
import {ResultListComponent} from './result-list/result_list.component';
import {OpendataComponent} from './opendata/opendata.component';
import {TCComponent} from './tc/tc.component';
import {HomeComponent} from './home/home.component';
import {HistoryDetailViewComponent} from './history/view-detail.component';
import {HistoryViewComponent} from './history/history.view.component';
import {OpentestViewComponent} from './result/view.component';
import {OpentestDetailViewComponent} from './result/view-detail.component';
import {SettingsComponent} from './user/settings.component';
import {DocuComponent} from './docu/docu.component';


export const routes: Routes = [{
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
        },
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
}, {
    path: '404',
    component: NotFoundComponent
}, {
    path: '**',
    redirectTo: '404'
}
];

export const routing: ModuleWithProviders = RouterModule.forRoot(
    routes, {onSameUrlNavigation: 'reload'}
    );
