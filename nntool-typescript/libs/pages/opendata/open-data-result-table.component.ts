import { NGXLogger } from 'ngx-logger';
import { Component, ViewChild, ElementRef, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { fromEvent } from 'rxjs';
import { map, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { SpringServerDataSource } from '../../core/services/table/spring-server.data-source';
import { SearchApiService } from '../../core/services/search-api.service';
import { DateParseService } from '@nntool-typescript/core/services/date-parse.service';
import { ProviderFilterResponse, BasicFilter, FilterType, FilterOption } from '../../core/models/provider-filter-response';
import { FormValues, FormValue } from '../../core/components/dynamic-form/dynamic-form.component';


@Component({
  templateUrl: './open-data-result-table.component.html',
  styleUrls: ['./open-data-result-table.component.less']
})
export class OpenDataResultTableComponent implements OnInit {
  public loading = false;

  public settings = {
    columns: {
      start_time: {
        title: 'Time',
        filter: false, // TODO: use this for search query?
        valuePrepareFunction: (cell, row) => {
          return this.dateParseService.parseDateIntoFormat(new Date(Date.parse(row.start_time)));
        }
      },
      open_data_uuid: {
        title: 'OpenDataUuid',
        filter: false,
        sort: false // throws exception: Caused by: ElasticsearchException[Elasticsearch exception [type=illegal_argument_exception, reason=Fielddata is disabled on text fields by default. Set fielddata=true on [open_data_uuid] in order to load fielddata in memory ...
      },
      'measurements.SPEED.rtt_info.average_ns': {
        // or median?
        title: 'Ping (ms)',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          if (row.measurements.SPEED && row.measurements.SPEED.rtt_info && row.measurements.SPEED.rtt_info.average_ns) {
            return (row.measurements.SPEED.rtt_info.average_ns / 1000000).toFixed(2);
          } else {
            return 'n/a';
          }
        }
      },
      'measurements.SPEED.throughput_avg_download_bps': {
        title: 'Download (Mbit/s)',
        filter: false,
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

  public tableSource: SpringServerDataSource;

  public showFilterFields: boolean = true;

  @ViewChild('fullTextSearchInput', { static: true }) private fullTextSearchInput: ElementRef;

  constructor(
    private logger: NGXLogger,
    private router: Router,
    private translationService: TranslateService,
    private searchApiService: SearchApiService,
    private dateParseService: DateParseService
  ) {
    this.tableSource = this.searchApiService.getServerDataSource();
  }

  private filterResponse: ProviderFilterResponse;

  public ngOnInit() {
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

      this.filterResponse = this.fetchFilterResponse();
  }

  onFormChangeCallback($event: FormValue[]) {
    if (this.tableSource && $event) {
      let queryString: string = "";
      $event.forEach((value, index) => {
        if (value.value && value.value !== "undefined") {
          if (queryString.length > 0) {
            queryString += " AND ";
          }
          if (value.queryString.includes("{}")) {
            queryString += value.queryString.replace(/{}/g, value.value);
          } else {
            queryString += value.queryString + value.value;
          }
        }
      });
      if (queryString.length > 0) {
        this.tableSource.setSearchQuery(queryString);
      } else {
        this.tableSource.removeSearchQuery();
      }
      this.tableSource.refresh();
    }
  }

  public switchSearchDisplay() {
    this.tableSource.removeSearchQuery();
    this.tableSource.refresh();
    this.showFilterFields = !this.showFilterFields;
  }

  public showOpenDataMeasurement(item: any) {
    this.router.navigate(['/open-data-results', item.open_data_uuid]);
  }

  public bulkExportAsCsv() {
    this.searchApiService.exportOpenDataMeasurements(
      this.tableSource.getSearchQuery(),
      this.tableSource.getPagingConf['page'],
      this.tableSource.getPagingConf['perPage'],
      'csv'
    );
  }

  public bulkExportAsJsonZip() {
    this.searchApiService.exportOpenDataMeasurements(
      this.tableSource.getSearchQuery(),
      this.tableSource.getPagingConf['page'],
      this.tableSource.getPagingConf['perPage'],
      'json.zip'
    );
  }

  public bulkExportAsYamlZip() {
    this.searchApiService.exportOpenDataMeasurements(
      this.tableSource.getSearchQuery(),
      this.tableSource.getPagingConf['page'],
      this.tableSource.getPagingConf['perPage'],
      'yaml.zip'
    );
  }

  private fetchFilterResponse(): ProviderFilterResponse {
    const ret = new ProviderFilterResponse();
    ret.filters = new Array<BasicFilter>(
      {
        filter_type: "DROPDOWN",
        key: "connection_type",
        query_string: "computed_network_info.network_type_group_name:",
        options: [
          { label: undefined, value: undefined },
          { label: "LAN", value: "LAN" },
          { label: "WLAN", value: "WLAN" },
          { label: "2G", value: "2G" },
          { label: "3G", value: "3G" },
          { label: "4G", value: "4G" }
        ]
      },
      {
        filter_type: "INPUT_TEXT",
        key: "system_uuid",
        query_string: "system_uuid:"
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "download_greater",
        query_string: "measurements.SPEED.throughput_avg_download_bps:>=",
        value_multiplier: 1000000
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "download_smaller",
        query_string: "measurements.SPEED.throughput_avg_download_bps:<=",
        value_multiplier: 1000000
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "upload_greater",
        query_string: "measurements.SPEED.throughput_avg_upload_bps:>=",
        value_multiplier: 1000000
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "upload_smaller",
        query_string: "measurements.SPEED.throughput_avg_upload_bps:<=",
        value_multiplier: 1000000
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "rtt_greater",
        query_string: "measurements.SPEED.rtt_info.average_ns:>=",
        value_multiplier: 1000000
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "rtt_smaller",
        query_string: "measurements.SPEED.rtt_info.average_ns:<=",
        value_multiplier: 1000000
      },
      {
        filter_type: "INPUT_DATE",
        key: "date_greater",
        query_string: "end_time:>="
      },
      {
        filter_type: "INPUT_DATE",
        key: "date_smaller",
        query_string: "end_time:<="
      },
      {
        filter_type: "INPUT_TEXT",
        key: "asn",
        query_string: "computed_network_info.public_ip_asn:"
      },
      {
        filter_type: "INPUT_TEXT",
        key: "provider",
        query_string: "computed_network_info.network_operator_name:"
      },
      {
        filter_type: "INPUT_TEXT",
        key: "country",
        query_string: "computed_network_info.network_country:"
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "frequency",
        query_string: "computed_network_info.frequency:"
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "ports_blocked_greater",
        query_string: "qos_advanced_evaluation.blocked_ports.total_count_blocked_ports:>="
      },
      {
        filter_type: "INPUT_NUMBER",
        key: "ports_blocked_smaller",
        query_string: "qos_advanced_evaluation.blocked_ports.total_count_blocked_ports:<="
      }
    );

    return ret;
  }
}
