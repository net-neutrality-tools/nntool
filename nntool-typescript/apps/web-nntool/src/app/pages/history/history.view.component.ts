import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ResultGroupResponse } from '../../@core/models/result.groups';
import { QoSMeasurementResult } from '../../@core/models/lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';
import { UserService } from '../../@core/services/user.service';

@Component({
  templateUrl: './history.view.component.html'
})
export class HistoryViewComponent implements OnInit {
  public loading: boolean;

  private measurementUuid: string;
  public response: ResultGroupResponse;

  private fullMeasurementResponse: any;
  public qosMeasurementResult: QoSMeasurementResult;

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService) {
    this.loading = true;
    this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
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
  }
}
