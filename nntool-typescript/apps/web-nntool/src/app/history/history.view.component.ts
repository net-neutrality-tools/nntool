import {MeasurementViewComponent} from '../measurement/view.component';
import {Component, OnInit} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../services/config.service';
import { RequestsService } from '../services/requests.service';
import { LoggerService, Logger } from '../services/log.service';
import { WebsiteSettings } from '../settings/settings.interface';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';
import { ResultGroupResponse } from './model/result.groups';

@Component({
    templateUrl: './history.view.component.html'
})
export class HistoryViewComponent extends MeasurementViewComponent implements OnInit{

    private configService: ConfigService;
    private requests: RequestsService;
    private logger: Logger = LoggerService.getLogger('HistoryViewComponent');
    private config: WebsiteSettings;
    private translationKey: string;
    private urlpath: string;
    private measurementUuid: string;
    private response: ResultGroupResponse;
    private loading: boolean;

    constructor(private translationService: TranslateService, private activatedRoute: ActivatedRoute,
                private userService: UserService) {
        super();
        this.loading = true;
        this.urlpath = '/history/';
        this.translationKey = 'RESULT.DETAIL';
        this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
    }

    protected resetData(cb?: () => void): void {}


    ngOnInit() {
        this.activatedRoute.paramMap.subscribe( paramMap => this.measurementUuid = paramMap.get('uuid'));
        this.userService.loadMeasurementDetail(this.measurementUuid).subscribe(
            (data: any) => {
                this.loading = false;
                this.response = data.data;
            }
        );
    }
}
