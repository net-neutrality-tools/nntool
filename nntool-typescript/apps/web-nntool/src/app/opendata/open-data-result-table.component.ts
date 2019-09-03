import { NGXLogger } from 'ngx-logger';
import { Component, ViewChild, ElementRef, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SearchApiService } from '../services/search-api.service';
import { SpringServerDataSource } from '../services/table/spring-server.data-source';
import { fromEvent } from 'rxjs';
import { map, filter, debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  templateUrl: './open-data-result-table.component.html',
  styleUrls: ['./open-data-result-table.component.less']
})
export class OpenDataResultTableComponent implements OnInit {
  private loading = false;

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

  @ViewChild('fullTextSearchInput', { static: true }) fullTextSearchInput: ElementRef;

  constructor(
    private logger: NGXLogger,
    private router: Router,
    private translationService: TranslateService,
    private searchApiService: SearchApiService
  ) {
    this.tableSource = this.searchApiService.getServerDataSource();
  }

  ngOnInit() {
    fromEvent(this.fullTextSearchInput.nativeElement, 'keyup')
      .pipe(
        map((event: any) => {
          return event.target.value;
        }),
        //filter(res => res.length > 2),
        debounceTime(500),
        distinctUntilChanged()
      )
      .subscribe((q: string) => {
        //this.logger.debug('q=' + q);
        if (q && q.length > 2) {
          this.tableSource.setSearchQuery(q);
        } else {
          this.tableSource.removeSearchQuery();
        }
        this.tableSource.refresh();
      });
  }

  showOpenDataMeasurement(item: any) {
    this.router.navigate(['/open-data-results', item.open_data_uuid]);
  }
}
