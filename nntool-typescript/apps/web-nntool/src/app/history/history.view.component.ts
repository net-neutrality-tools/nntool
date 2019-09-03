import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../services/config.service';
import { RequestsService } from '../services/requests.service';
import { UserService } from '../services/user.service';
import { WebsiteSettings } from '../settings/settings.interface';
import { ResultGroupResponse } from './model/result.groups';

@Component({
  templateUrl: './history.view.component.html'
})
export class HistoryViewComponent implements OnInit {
  private configService: ConfigService;
  private requests: RequestsService;
  private config: WebsiteSettings;
  private translationKey: string;
  private urlpath: string;
  private measurementUuid: string;
  private response: ResultGroupResponse;
  private loading: boolean;

  constructor(
    private translationService: TranslateService,
    private activatedRoute: ActivatedRoute,
    private userService: UserService
  ) {
    this.loading = true;
    this.urlpath = '/history/';
    this.translationKey = 'RESULT.DETAIL';
    this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
  }

  public ngOnInit() {
    this.activatedRoute.paramMap.subscribe(paramMap => (this.measurementUuid = paramMap.get('uuid')));
    this.userService.loadMeasurementDetail(this.measurementUuid).subscribe((data: any) => {
      this.loading = false;
      this.response = data.data;
    });
  }

  // protected resetData(cb?: () => void): void {}
}
