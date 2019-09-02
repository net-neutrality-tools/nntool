import { NGXLogger } from 'ngx-logger';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SearchApiService } from '../services/search-api.service';
import { SpringServerDataSource } from '../services/table/spring-server.data-source';

@Component({
  templateUrl: './open-data-result-list.component.html',
  styleUrls: ['./open-data-result-list.component.less']
})
export class OpenDataResultListComponent {
  private loading = false;

  public translationKey = 'RESULT_LIST';

  settings = {
    columns: {
      start_time: {
        title: 'Time',
        filter: false, // TODO: use this for search query?
        valuePrepareFunction: (cell, row) => {
          return new Date(Date.parse(row.start_time)).toLocaleString();
        }
      },
      open_data_uuid: {
        title: 'OpenDataUuid',
        filter: false,
        sort: false // throws exception: Caused by: ElasticsearchException[Elasticsearch exception [type=illegal_argument_exception, reason=Fielddata is disabled on text fields by default. Set fielddata=true on [open_data_uuid] in order to load fielddata in memory ...
      },
      'measurements.SPEED.rtt_info.average_ns': {
        // or median?
        title: 'RTT (ms)',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          return (row.measurements.SPEED.rtt_info.average_ns / 1000000).toFixed(2);
        }
      },
      'measurements.SPEED.throughput_avg_download_bps': {
        title: 'Download (Mbit/s)',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          return (row.measurements.SPEED.throughput_avg_download_bps / 1000 / 1000).toFixed(2);
        }
      },
      'measurements.SPEED.throughput_avg_upload_bps': {
        title: 'Upload (Mbit/s)',
        filter: false,
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
    private searchApiService: SearchApiService
  ) {
    this.tableSource = this.searchApiService.getServerDataSource();
  }

  showOpenDataMeasurement(item: any) {
    this.router.navigate(['/open-data-results', item.open_data_uuid]);
  }
}
