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

import { NGXLogger } from 'ngx-logger';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SpringServerDataSource } from '../../core/services/table/spring-server.data-source';
import { UserService } from '../../core/services/user.service';
import { ResultApiService } from '../../core/services/result-api.service';
import { DateParseService } from '@nntool-typescript/core/services/date-parse.service';
import { isElectron } from '@nntool-typescript/utils';

@Component({
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.less']
})
export class HistoryComponent {
  private loading = false;

  settings = {
    columns: {
      start_time: {
        title: 'Time',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          return this.dateParseService.parseDateIntoFormat(new Date(Date.parse(row.start_time)));
        }
      },
      'device_info.device_code_name': isElectron() ? {
        title: 'Connection',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          return row.network_type_name;
        }
      }:{},
      'measurements.SPEED.rtt_average_ns': {
        // or median?
        title: 'Ping (ms)',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          if (row.measurements.SPEED && row.measurements.SPEED.rtt_average_ns) {
            return (row.measurements.SPEED.rtt_average_ns / 1000000).toFixed(2);
          } else {
            return 'n/a';
          }
        }
      },
      'measurements.SPEED.throughput_avg_download_bps': {
        title: 'Download (Mbit/s)',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          if (row.measurements.SPEED && row.measurements.SPEED.throughput_avg_download_bps) {
            return (row.measurements.SPEED.throughput_avg_download_bps / 1000 / 1000).toFixed(2);
          } else {
            return 'n/a';
          }
        }
      },
      'measurements.SPEED.throughput_avg_upload_bps': {
        title: 'Upload (Mbit/s)',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          if (row.measurements.SPEED && row.measurements.SPEED.throughput_avg_upload_bps) {
            return (row.measurements.SPEED.throughput_avg_upload_bps / 1000 / 1000).toFixed(2);
          } else {
            return 'n/a';
          }
        }
      }
    }
  };

  tableSource: SpringServerDataSource;

  constructor(
    private logger: NGXLogger,
    private router: Router,
    private translationService: TranslateService,
    private userService: UserService,
    private resultApiService: ResultApiService,
    private dateParseService: DateParseService
  ) {
    this.tableSource = this.resultApiService.getServerDataSource(this.userService.user.uuid);
  }

  showMeasurement(item: any) {
    this.router.navigate(['/history', item.uuid]);
  }
}
