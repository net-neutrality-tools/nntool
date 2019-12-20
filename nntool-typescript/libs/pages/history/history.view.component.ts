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
import { UserService } from '../../core/services/user.service';
import { ConfigService } from '../../core/services/config.service';
import { WebsiteSettings } from '../../core/models/settings/settings.interface';

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
    this.config = this.configService.getConfig();
  }

  public ngOnInit() {
    this.activatedRoute.paramMap.subscribe(paramMap => (this.measurementUuid = paramMap.get('uuid')));
    this.userService.loadMeasurementDetail(this.measurementUuid).subscribe((data: any) => {
      this.loading = false;
      this.response = data.data;

      this.hasShareLinks =
        this.config.socialMediaSettings &&
        this.config.socialMediaSettings.history &&
        this.config.socialMediaSettings.history.medias
          ? true
          : false;
    });

    this.userService.loadFullMeasurement(this.measurementUuid).subscribe((data: any) => {
      this.fullMeasurementResponse = data.data;
      this.qosMeasurementResult = this.fullMeasurementResponse.measurements.QOS;
    });
  }

  public getUrl() {
    return window.location.href;
  }
}
