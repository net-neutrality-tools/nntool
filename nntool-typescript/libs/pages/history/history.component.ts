import { NGXLogger } from 'ngx-logger';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SpringServerDataSource } from '../../core/services/table/spring-server.data-source';
import { UserService } from '../../core/services/user.service';
import { ResultApiService } from '../../core/services/result-api.service';

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
    private resultApiService: ResultApiService
  ) {
    this.tableSource = this.resultApiService.getServerDataSource(this.userService.user.uuid);
  }

  showMeasurement(item: any) {
    this.router.navigate(['/history', item.uuid]);
  }
}
