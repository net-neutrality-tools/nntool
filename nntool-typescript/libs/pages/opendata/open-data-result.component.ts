import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ResultGroupResponse } from '../../core/models/result.groups';
import { QoSMeasurementResult } from '../../core/models/lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';
import { SearchApiService } from '../../core/services/search-api.service';
import { NGXLogger } from 'ngx-logger';

@Component({
  templateUrl: './open-data-result.component.html'
})
export class OpenDataResultComponent implements OnInit {
  private openDataUuid: string;
  public response: ResultGroupResponse;
  public loading: boolean;

  private fullMeasurementResponse: any;
  public qosMeasurementResult: QoSMeasurementResult;

  constructor(
    private logger: NGXLogger,
    private activatedRoute: ActivatedRoute,
    private searchApiService: SearchApiService
  ) {
    this.loading = true;
    this.openDataUuid = activatedRoute.snapshot.paramMap.get('openDataUuid');
  }

  public ngOnInit() {
    this.activatedRoute.paramMap.subscribe(paramMap => (this.openDataUuid = paramMap.get('openDataUuid')));

    this.searchApiService.getSingleGroupedOpenDataMeasurement(this.openDataUuid).subscribe((data: any) => {
      this.loading = false;
      this.response = data.data;
    });

    this.searchApiService.getSingleOpenDataMeasurement(this.openDataUuid).subscribe((data: any) => {
      this.fullMeasurementResponse = data.data;
      this.qosMeasurementResult = this.fullMeasurementResponse.measurements.QOS;
    });
  }

  public exportAsCsv() {
    this.searchApiService.exportSingleOpenDataMeasurement(this.openDataUuid, 'csv');
  }

  public exportAsJson() {
    this.searchApiService.exportSingleOpenDataMeasurement(this.openDataUuid, 'json');
  }

  public exportAsYaml() {
    this.searchApiService.exportSingleOpenDataMeasurement(this.openDataUuid, 'yaml');
  }
}
