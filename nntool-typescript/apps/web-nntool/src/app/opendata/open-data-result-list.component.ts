import { NGXLogger } from 'ngx-logger';
import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SearchApiService } from '../services/search-api.service.';
import { SpringServerDataSource } from '../services/table/spring-server.data-source';

@Component({
  templateUrl: './open-data-result-list.component.html',
  styleUrls: ['./open-data-result-list.component.less']
})
export class OpenDataResultListComponent {
  private loading = false;

  settings = {
    hideSubHeader: true,
    //noDataMessage: , // TODO: translate
    /*attr: {
      //class: 'no-stack'
    },*/
    pager: {
      display: true,
      perPage: 25
    },
    mode: 'external',
    actions: {
      add: false,
      edit: false,
      delete: false
    },
    columns: {
      start_time: {
        title: 'Time',
        filter: false // TODO: use this for search query?
      },
      open_data_uuid: {
        title: 'OpenDataUuid',
        filter: false,
        sort: false // throws exception: Caused by: ElasticsearchException[Elasticsearch exception [type=illegal_argument_exception, reason=Fielddata is disabled on text fields by default. Set fielddata=true on [open_data_uuid] in order to load fielddata in memory ...
      },
      'measurements.SPEED.rtt_info.medianNs': {
        // or average?
        title: 'RTT',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          return (row.measurements.SPEED.rtt_info.medianNs / 1000000).toFixed(2);
        }
      },
      'measurements.SPEED.throughput_avg_download_bps': {
        title: 'Download',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          return (row.measurements.SPEED.throughput_avg_download_bps / 1000 / 1000).toFixed(2);
        }
      },
      'measurements.SPEED.throughput_avg_upload_bps': {
        title: 'Upload',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          return (row.measurements.SPEED.throughput_avg_upload_bps / 1000 / 1000).toFixed(2);
        }
      }
    }
  };

  tableSource: SpringServerDataSource;

  constructor(
    protected logger: NGXLogger,
    private router: Router,
    private translationService: TranslateService,
    private searchApiService: SearchApiService
  ) {
    this.tableSource = this.searchApiService.getServerDataSource();
  }

  public ngOnInit() {}

  showOpenDataMeasurement(event: any) {
    let measurement = event.data;
    this.router.navigate(['/open-data-results', measurement.open_data_uuid]);
  }
}
