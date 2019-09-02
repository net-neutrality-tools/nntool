import { NGXLogger } from 'ngx-logger';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SpringServerDataSource } from '../services/table/spring-server.data-source';
import { ResultApiService } from '../services/result-api.service';
import { UserService } from '../services/user.service';

@Component({
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.less']
})
export class HistoryComponent {
  private loading = false;

  public translationKey = 'HISTORY';

  settings = {
    columns: {
      start_time: {
        title: 'Time',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          return new Date(Date.parse(row.start_time)).toLocaleString();
        }
      },
      'device_info.device_code_name': {
        title: 'Device',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          return row.device_info.device_code_name;
        }
      },
      'measurements.SPEED.rtt_average_ns': {
        // or median?
        title: 'RTT (ms)',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          return (row.measurements.SPEED.rtt_average_ns / 1000000).toFixed(2);
        }
      },
      'measurements.SPEED.throughput_avg_download_bps': {
        title: 'Download (Mbit/s)',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          return (row.measurements.SPEED.throughput_avg_download_bps / 1000 / 1000).toFixed(2);
        }
      },
      'measurements.SPEED.throughput_avg_upload_bps': {
        title: 'Upload (Mbit/s)',
        filter: false,
        sort: false,
        valuePrepareFunction: (cell, row) => {
          return (row.measurements.SPEED.throughput_avg_upload_bps / 1000 / 1000).toFixed(2);
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
    private resultApiService: ResultApiService
  ) {
    this.tableSource = this.resultApiService.getServerDataSource(this.userService.user.uuid);
  }

  showMeasurement(item: any) {
    this.router.navigate(['/history', item.uuid]);
  }
}
