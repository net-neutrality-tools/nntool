import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';
import { NvD3Component } from 'ng2-nvd3';
import { finalize } from 'rxjs/operators';
declare let d3: any;

import { ResultListComponent } from '../result-list/result_list.component';
import { ConfigService } from '../services/config.service';
import { LoggerService } from '../services/log.service';
import { RequestsService } from '../services/requests.service';
import { UserInfo, UserService } from '../services/user.service';
import { PaginationAPI } from '../test/models/api/pagination.api';
import { ResponseAPI } from '../test/models/api/response.api';
import {
  BriefMeasurementResponseAPI,
  BriefSubMeasurement
} from '../test/models/results/brief-measurement-response.api';

@Component({
  templateUrl: '../result-list/result_list.component.html'
})
export class HistoryComponent extends ResultListComponent implements OnInit {
  get uuid(): null | string {
    if (this.queryParams && this.queryParams.show_uuid) {
      this.showClientUUID = this.queryParams.show_uuid;
    } else {
      this.showClientUUID = null;
    }
    return this.showClientUUID ? this.showClientUUID : this.user ? this.user.uuid : null;
  }

  get user(): UserInfo {
    return this.userService.user ? this.userService.user : null;
  }

  @ViewChild(NvD3Component, { static: false })
  public nvD3: NvD3Component;

  private showClientUUID: null | string = null;
  private transSpeed = '';
  private transUp = '';
  private transDown = '';

  constructor(
    elementRef: ElementRef,
    router: Router,
    configService: ConfigService,
    requests: RequestsService,
    translationService: TranslateService,
    activatedRoute: ActivatedRoute,
    private userService: UserService
  ) {
    super(elementRef, router, configService, requests, translationService, activatedRoute);
    this.logger = LoggerService.getLogger('HistoryComponent');
    this.urlpath = '/history/';
    this.translationKey = 'HISTORY';
    this.graph = {
      data: [
        {
          key: this.transDown,
          // curveType: 'download',
          area: false,
          values: []
        },
        {
          key: this.transUp,
          // curveType: 'upload',
          area: true,
          values: []
        }
      ],
      options: {
        chart: {
          type: 'lineChart',
          height: 450,
          clipEdge: true,
          // yDomain: [0, 100],
          x(d: any) {
            if (d) {
              return d.x;
            }
          },
          y(d: any) {
            if (d) {
              return d.y;
            }
          },
          showValues: true,
          // duration: 500,
          xAxis: {
            showMaxMin: false,
            tickFormat: (d: any) => {
              return d3.time.format('%Y-%m-%d')(new Date(d));
            }
          },
          yAxis: {
            showMaxMin: true,
            axisLabel: this.transSpeed,
            axisLabelDistance: 0
          },
          tooltip: {
            contentGenerator: (d: any) => {
              const unit: string = ' ' + this.transSpeed;
              const key: string = d.series[0].key;

              return (
                "<div style='color: " +
                d.point.color +
                "'>" +
                '<h3>' +
                key +
                '</h3>' +
                '<h4>' +
                d.point.y +
                unit +
                '</h4>' +
                '</div>'
              );
            }
          }
        }
      }
    };
  }

  public ngOnInit() {
    this.showClientUUID = null;
    this.initLang();
    super.ngOnInit();
  }

  public openResult(uuid: string) {
    if (!uuid) {
      this.logger.warn('No uuid');
      return false;
    }
    this.router.navigate(['/history', uuid]);
    return false;
  }

  protected langChange(): void {
    this.initLang();
    super.langChange();
  }

  protected searchExecute(more: boolean): void {
    this.logger.debug('searchExec()');
    if (this.loading) {
      // Already loading
      this.logger.warn('already loading');
      return;
    }
    this.loading = true;
    // this.graph.data[0].values = [];
    // this.graph.data[1].values = [];
    if (!this.uuid) {
      this.logger.debug('No user');
      if (!more) {
        this.configService.clearArray(this.results);
      }
      this.loading = false;
      return;
    }
    const user: UserInfo = new UserInfo();
    user.uuid = this.uuid;

    this.userService
      .loadMeasurements(user)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe(
        (data: ResponseAPI<PaginationAPI<BriefMeasurementResponseAPI>>) => {
          this.logger.debug(data);
          if (!more) {
            this.configService.clearArray(this.results);
          }
          for (const briefMeasurementResponse of data.data.content) {
            let name = '';

            if (briefMeasurementResponse) {
              if (briefMeasurementResponse.device_info) {
                if (briefMeasurementResponse.device_info.device_full_name) {
                  name += `${briefMeasurementResponse.device_info.device_full_name} `;
                }
                if (briefMeasurementResponse.device_info.os_name) {
                  name += `${briefMeasurementResponse.device_info.os_name} `;
                }
              }
              if (briefMeasurementResponse.type) {
                name += ` ${briefMeasurementResponse.type}`;
              }
            }

            let speedResult: BriefSubMeasurement;
            if (briefMeasurementResponse.measurements) {
              speedResult = briefMeasurementResponse.measurements.SPEED;
            }

            this.results.push({
              time: briefMeasurementResponse.start_time,
              /* tslint:disable:no-string-literal */
              down: speedResult ? '' + Math.round(speedResult['throughput_avg_download_bps'] / (1000 * 10)) / 100 : '',
              up: speedResult ? '' + Math.round(speedResult['throughput_avg_upload_bps'] / (1000 * 10)) / 100 : '',
              ping: speedResult ? '' + Math.round(speedResult['rtt_average_ns'] / (1000 * 1000)) : '',
              /* tslint:enable:no-string-literal */
              name,
              signal: null,
              uuid: briefMeasurementResponse.uuid
            });
          }
          this.updateGraphData();
        },
        (error: any) => {
          const unknownUser: any = (): void => {
            this.logger.warn('Unknown user - deleting uuid');
            if (!this.showClientUUID) {
              this.userService.user.uuid = null;
              this.userService.save(this.userService.user);
            }
            return this.searchExecute(more);
          };
          if (typeof error === 'string' && error.startsWith('403 -')) {
            return unknownUser();
          } else if (error && error.status === 403) {
            return unknownUser();
          } else {
            this.logger.error('Failed to load', error);
            // this.loading = false;
          }
        }
      );
  }

  private initLang(): void {
    this.subs.push(
      this.translationService
        .get(['WEBSITE.UNITS.SPEED_MBPS', 'HISTORY.GRAPH.DOWNLOAD', 'HISTORY.GRAPH.UPLOAD'])
        .subscribe((res: any) => {
          this.transSpeed = res['WEBSITE.UNITS.SPEED_MBPS'];
          this.graph.options.chart.yAxis.axisLabel = this.transSpeed;
          this.transDown = res['HISTORY.GRAPH.DOWNLOAD'];
          this.graph.data[0].key = this.transDown;
          this.transUp = res['HISTORY.GRAPH.UPLOAD'];
          this.graph.data[1].key = this.transUp;
        })
    );
  }

  private maxMinGraph(nvD3: NvD3Component): void {
    let max = 1;
    let min = 0;

    for (const g of nvD3.data) {
      if (g.disabled) {
        continue;
      }
      const tmpMax: number = d3.max(g.values, (d: any) => d.y);
      const tmpMin: number = d3.min(g.values, (d: any) => d.y);
      max = Math.max(max, tmpMax);
      min = Math.min(min, tmpMin);
    }

    max = Math.max(0, Math.round(max * 0.12) * 10 + 5);
    min = Math.max(0, Math.round(min * 0.08) * 10);
    nvD3.chart.yDomain([min, max]);
    setTimeout(() => {
      nvD3.chart.update();
    }, 0);
  }

  private updateGraphData(): void {
    if (!this.config.result_list || !this.config.result_list.history_graph_enabled) {
      // Not showing graph anyway
      return;
    }
    const down: any[] = (this.graph.data[0].values = []);
    const up: any[] = (this.graph.data[1].values = []);

    for (const res of this.results) {
      if (res.down && res.time) {
        down.push({
          x: new Date(res.time),
          y: Number.parseFloat(res.down)
        });
      }
      if (res.up && res.time) {
        up.push({
          x: new Date(res.time),
          y: Number.parseFloat(res.up)
        });
      }
    }
    this.graph.data = [this.graph.data[0], this.graph.data[1]];
    setTimeout(() => {
      if (this.nvD3 && this.nvD3) {
        // this.nvD3.chart.yAxis.domain(newDomain);
        // this.logger.debug(this.nvD3.chart.yAxis.domain());
        // this.logger.debug(this.nvD3.chart.yDomain(newDomain));
        this.nvD3.chart.legend.dispatch.on('legendClick', () => {
          setTimeout(() => {
            this.maxMinGraph(this.nvD3);
          }, 0);
        });
        this.maxMinGraph(this.nvD3);
      }
    }, 1);
  }
}
