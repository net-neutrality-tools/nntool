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

  public exportAsCsv(coarse: boolean = false) {
    this.searchApiService.exportSingleOpenDataMeasurement(this.openDataUuid, coarse, 'csv');
  }

  public exportAsJson(coarse: boolean = false) {
    this.searchApiService.exportSingleOpenDataMeasurement(this.openDataUuid, coarse, 'json');
  }

  public exportAsYaml(coarse: boolean = false) {
    this.searchApiService.exportSingleOpenDataMeasurement(this.openDataUuid, coarse, 'yaml');
  }
}
