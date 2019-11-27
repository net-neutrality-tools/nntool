import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { SpringServerDataSource } from '../../@core/services/table/spring-server.data-source';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { StatisticApiService } from '../../@core/services/statistic-api.service';
import { FormGroup, FormBuilder, FormArray, FormControl } from '@angular/forms';
import { ProviderFilterResponse, ProviderFilterResponseWrapper } from '../../@core/models/provider-filter-response';

@Component({
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.less']
})
export class StatisticsComponent implements OnInit {
  public settings = {
    //hideSubHeader: false,
    columns: {
      provider_name: {
        title: 'Provider',
        filter: true
      },
      provider_asn: {
        title: 'ASN',
        filter: true
      },
      mcc_mnc: {
        title: 'MCC-MNC',
        filter: true
      },
      country_code: {
        title: 'Country',
        filter: true
      },
      download_bps: {
        title: 'Download',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          if (row.download_bps) {
            return (row.download_bps / 1000_000).toFixed(2);
          } else {
            return 'n/a';
          }
        }
      },
      upload_bps: {
        title: 'Upload',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          if (row.upload_bps) {
            return (row.upload_bps / 1000_000).toFixed(2);
          } else {
            return 'n/a';
          }
        }
      },
      rtt_ns: {
        title: 'RTT',
        filter: false,
        valuePrepareFunction: (cell, row) => {
          if (row.rtt_ns) {
            return (row.rtt_ns / 1000_000).toFixed(2);
          } else {
            return 'n/a';
          }
        }
      },
      signal_strength_dbm: {
        title: 'Signal',
        filter: false
      },
      count: {
        title: 'Count',
        filter: false
      }
    }
  };

  public tableSource: SpringServerDataSource;

  constructor(
    private logger: NGXLogger,
    private router: Router,
    private translationService: TranslateService,
    private statisticApiService: StatisticApiService,
    private formBuilder: FormBuilder
  ) {
    this.tableSource = this.statisticApiService.getProviderStatisticsServerDataSource();
  }

  public filterResponse: ProviderFilterResponse;

  public dynamicForm: FormGroup;

  public ngOnInit(): void {
    
    this.statisticApiService.getProviderFilterConfiguration().subscribe(
      (dataWrapper: ProviderFilterResponseWrapper) => {
        this.filterResponse = dataWrapper.data;
        this.dynamicForm = this.formBuilder.group({
          filters: this.formBuilder.array([this.formBuilder.control('')])
        });

        for (let filter of this.filterResponse.filters) {
          let control: FormControl = this.formBuilder.control('');
          (this.dynamicForm.get('filters') as FormArray).push(control);
        }

        this.dynamicForm.valueChanges.subscribe(val => {
          this.onFormChangeCallback(val);
        });

        this.filterResponse.filters.forEach((filter, index) => {
          (this.dynamicForm.get("filters") as FormArray).at(index).setValue(filter.default_value);
        });

      },
      (error: string) => {
        this.logger.error(error);
      }
    );    
  }

  onFormChangeCallback(form: any) {
    if (form && form.filters) {
      this.filterResponse.filters.forEach((filter, index) => {
        if (form.filters[index] && form.filters[index] !== "") {
          this.tableSource.setHttpParam(filter.key, form.filters[index]);
        }
      });
      this.tableSource.refresh();
    }
  }

}
