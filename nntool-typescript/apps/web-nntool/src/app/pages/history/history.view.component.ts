import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ResultGroupResponse } from '../../@core/models/result.groups';
import { QoSMeasurementResult } from '../../@core/models/lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';
import { UserService } from '../../@core/services/user.service';
import { ConfigService } from '../../@core/services/config.service';
import { WebsiteSettings } from '../../@core/models/settings/settings.interface';

@Component({
  templateUrl: './history.view.component.html'
})
export class HistoryViewComponent implements OnInit {
  public loading: boolean;

  public response: ResultGroupResponse;
  public hasShareLinks: boolean = false;
  public qosMeasurementResult: QoSMeasurementResult;

  private measurementUuid: string;
  private fullMeasurementResponse: any;
  private config: WebsiteSettings;

  constructor(
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private configService: ConfigService
  ) {
    this.loading = true;
    this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
    this.config = configService.getConfig();
  }

  public ngOnInit() {
    this.activatedRoute.paramMap.subscribe(paramMap => (this.measurementUuid = paramMap.get('uuid')));
    this.userService.loadMeasurementDetail(this.measurementUuid).subscribe((data: any) => {
      this.loading = false;
      this.response = data.data;
    });

    this.userService.loadFullMeasurement(this.measurementUuid).subscribe((data: any) => {
      this.fullMeasurementResponse = data.data;
      this.qosMeasurementResult = this.fullMeasurementResponse.measurements.QOS;
    });

    this.hasShareLinks = this.config.socialMediaSettings && this.config.socialMediaSettings.history ? true : false;
  }

  public getUrl() {
    return window.location.href;
  }
}
