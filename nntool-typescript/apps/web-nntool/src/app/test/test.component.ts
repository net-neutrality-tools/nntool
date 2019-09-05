import { Component, ElementRef, NgZone, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';
import { DeviceDetectorService } from 'ngx-device-detector';
import { AppService } from '../services/app.service';
import { ConfigService } from '../services/config.service';
import { LocationService } from '../services/location.service';
import { RequestsService } from '../services/requests.service';
import { TestService } from '../services/test/test.service';
import { UserService } from '../services/user.service';
import { TestComponentStatus } from '../testing/enums/test-component-status.enum';
import { PortBlockingTestState } from '../testing/tests-implementation/port-blocking/port-blocking-test-state';
import { SpeedTestState } from '../testing/tests-implementation/speed/speed-test-state';
import { BaseNetTestComponent } from './base_test.component';
import { MeasurementSettings } from './models/measurement-settings';
import { MeasurementResultNetworkPointInTimeAPI } from './models/measurements/measurement-result-network-point-in-time.api';
import { TimeBasedResultAPI } from './models/measurements/time-based-result.api';
import { ServerSelectionComponent } from './test.server_selection';
import { SpeedMeasurementPeer } from './models/server-selection/speed-measurement-peer';
import { MeasurementTypeParameters, LmapOption } from '../@core/models/lmap/models/shared/lmap-option.model';
import { LmapControl } from '../@core/models/lmap/models/lmap-control.model';
import { LmapTask } from '../@core/models/lmap/models/lmap-control/lmap-task.model';
import { SpeedMeasurementResult } from '../@core/models/lmap/models/lmap-report/lmap-result/extensions/speed-measurement-result.model';
import { QoSMeasurementResult } from '../@core/models/lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';
import { ConnectionInfo } from '../@core/models/lmap/models/lmap-report/lmap-result/extensions/connection-info.model';
import { RttInfo } from '../@core/models/lmap/models/lmap-report/lmap-result/extensions/rtt-info.model';
import { LmapReport } from '../@core/models/lmap/models/lmap-report.model';
import { LmapResult } from '../@core/models/lmap/models/lmap-report/lmap-result.model';

export { TestGuard } from './test.guard';

@Component({
  templateUrl: './test.component.html'
})
export class NetTestComponent extends BaseNetTestComponent implements OnInit {
  @ViewChild(ServerSelectionComponent, { static: false }) public serverSelectionComponent: ServerSelectionComponent;
  public speedConfig: MeasurementSettings = undefined; // TODO: change to measurement configuration
  public qosConfig: MeasurementTypeParameters = undefined; // TODO: change to measurement configuration

  protected measurementControl: LmapControl = undefined;

  private readonly webNetworkType: number = 98;
  private readonly paramSpeed: string = 'parameters_speed';
  private readonly paramServerPort: string = 'server_port';
  private readonly paramServerAddress: string = 'server_addr';
  private readonly paramCollector: string = 'result_collector_base_url';
  private speedControl: LmapTask = undefined;
  private qosControl: LmapTask = undefined;
  private testResults: Array<SpeedMeasurementResult | QoSMeasurementResult> = [];
  private startTimeStamp: string = undefined;
  private selectedSpeedMeasurementPeer: SpeedMeasurementPeer = undefined;

  constructor(
    testService: TestService,
    configService: ConfigService,
    userService: UserService,
    translateService: TranslateService,
    requests: RequestsService,
    elementRef: ElementRef,
    zone: NgZone,
    activatedRoute: ActivatedRoute,
    appService: AppService,
    private locationService: LocationService,
    private deviceService: DeviceDetectorService
  ) {
    super(
      testService,
      configService,
      userService,
      translateService,
      requests,
      elementRef,
      zone,
      activatedRoute,
      appService
    );
  }

  public ngOnInit() {
    super.ngOnInit();
    this.screenNr = 1;
  }

  public agree(): void {
    super.agree();
  }

  public handleTestSeriesStatusChange(testComponentStatus: TestComponentStatus) {
    this.testInProgress = testComponentStatus !== TestComponentStatus.WAITING;
    if (testComponentStatus === TestComponentStatus.REQUESTS_CONFIG && this.user.acceptTC) {
      this.requestMeasurement();
    }
    if (testComponentStatus === TestComponentStatus.WAITING && this.user.acceptTC) {
      this.testSeriesFinished();
    }
  }

  public speedTestFinished(speedTestResult: SpeedTestState): void {
    const speedMeasurementResult: SpeedMeasurementResult = new SpeedMeasurementResult();

    speedMeasurementResult.deserialize_type = 'speed_result';
    speedMeasurementResult.reason = null;
    speedMeasurementResult.relative_end_time_ns = null;
    speedMeasurementResult.relative_start_time_ns = null;
    speedMeasurementResult.status = null;
    speedMeasurementResult.rtt_info = new RttInfo();
    speedMeasurementResult.connection_info = new ConnectionInfo();
    speedMeasurementResult.connection_info.identifier = this.selectedSpeedMeasurementPeer.identifier;

    if (typeof speedTestResult.completeTestResult !== 'undefined') {
      const completeResult = speedTestResult.completeTestResult;
      if (completeResult.download_info !== undefined && completeResult.download_info.length > 0) {
        const currentDownload = completeResult.download_info[completeResult.download_info.length - 1];
        // we sent the duration (not the duration_total), as the mbps are calculated from the duration => the total duration can be calculated from the relative starttimes
        speedMeasurementResult.duration_download_ns = currentDownload.duration_ns;
        speedMeasurementResult.bytes_download = currentDownload.bytes;
        speedMeasurementResult.bytes_download_including_slow_start = currentDownload.bytes_including_slow_start;
      }
      if (completeResult.rtt_info !== undefined) {
        const currentRtt = completeResult.rtt_info;
        speedMeasurementResult.duration_rtt_ns = currentRtt.duration_ns;
        speedMeasurementResult.rtt_info = {
          rtts: [
            {
              rtt_ns: speedTestResult.ping * 1000 * 1000,
              relative_time_ns: undefined
            }
          ],
          requested_num_packets: 10, // TODO: fill w/server setting
          num_sent: currentRtt.num_sent,
          num_received: currentRtt.num_received,
          num_error: currentRtt.num_error,
          num_missing: currentRtt.num_missing,
          packet_size: currentRtt.packet_size,
          average_ns: currentRtt.average_ns,
          maximum_ns: currentRtt.max_ns,
          median_ns: currentRtt.median_ms,
          minimum_ns: currentRtt.min_ns,
          standard_deviation_ns: currentRtt.standard_deviation_ns
        };
      }
      if (completeResult.upload_info !== undefined && completeResult.upload_info.length > 0) {
        const currentUpload = completeResult.upload_info[completeResult.upload_info.length - 1];
        // we sent the duration (not the duration_total), as the mbps are calculated from the duration => the total duration can be calculated from the relative starttimes
        speedMeasurementResult.duration_upload_ns = currentUpload.duration_ns;
        speedMeasurementResult.bytes_upload = currentUpload.bytes;
        speedMeasurementResult.bytes_upload_including_slow_start = currentUpload.bytes_including_slow_start;
      }
    } else {
      // TODO: probably remove this!
      speedMeasurementResult.bytes_download = speedTestResult.downBit / 8;
      speedMeasurementResult.bytes_upload = speedTestResult.upBit / 8;
      speedMeasurementResult.rtt_info.rtts = [
        {
          rtt_ns: speedTestResult.ping * 1000 * 1000,
          relative_time_ns: undefined
        }
      ];
    }

    this.testResults.push(speedMeasurementResult);
  }

  public portBlockingTestFinished(portBlockingTestResult: PortBlockingTestState): void {
    const qosMeasurementResult: QoSMeasurementResult = new QoSMeasurementResult();
    qosMeasurementResult.deserialize_type = 'qos_result';
    qosMeasurementResult.reason = null;
    qosMeasurementResult.relative_end_time_ns = null;
    qosMeasurementResult.relative_start_time_ns = null;
    qosMeasurementResult.status = null;
    qosMeasurementResult.results = [];

    for (const port of portBlockingTestResult.types[0].ports) {
      qosMeasurementResult.results.push({
        udp_result_out_num_packets: 1,
        udp_objective_out_num_packets: 1,
        qos_test_uid: port.uid,
        test_type: 'udp',
        udp_result_out_response_num_packets: port.reachable ? 1 : 0,
        udp_result_out_packet_loss_rate: port.reachable ? 0 : 100,
        udp_objective_out_port: port.number
      });
    }

    this.testResults.push(qosMeasurementResult);
  }

  private requestMeasurement(): void {
    this.selectedSpeedMeasurementPeer = this.serverSelectionComponent.getCurrentSpeedServer();
    this.testService
      .newMeasurement(undefined, this.selectedSpeedMeasurementPeer)
      .subscribe(response => {
        this.processTestControl(response);
        this.measurementControl = response;
        this.locationService.startTracking();
        this.startTimeStamp = new Date().toJSON().slice(0, -1);
      });
    this.measurementLink = null;
  }

  private processTestControl(measurementControl: LmapControl): void {
    if (measurementControl && measurementControl.tasks) {
      measurementControl.tasks.forEach((task: LmapTask) => {
        switch (task.name) {
          case 'SPEED':
            this.speedConfig = new MeasurementSettings();
            for (const i in task.option) {
              const option: LmapOption = task.option[i];
              /* tslint:disable:no-string-literal */
              if (option.name === this.paramCollector) {
                this.speedConfig.collectorAddress = option.value;
              } else if (option.name === this.paramServerAddress) {
                this.speedConfig.serverAddress = option.value;
              } else if (option.name === this.paramServerPort) {
                this.speedConfig.serverPort = option.value;
              } else if (option.name === this.paramSpeed) {
                this.speedConfig.speedConfig = option['measurement-parameters'].measurement_configuration;
              }
              /* tslint:enable:no-string-literal */
            }
            this.speedControl = task;
            break;
          case 'QOS':
            this.qosConfig = task.option.reduce((config: any, option: LmapOption) => {
              /* tslint:disable:no-string-literal */
              return option.name === 'parameters_qos' ? option['measurement-parameters']['objectives'] : config;
              /* tslint:enable:no-string-literal */
            }, {});
            this.qosControl = task;
            break;
        }
      });
    }
  }

  private testSeriesFinished(): void {
    if (this.testResults.length === 0) {
      return;
    }

    const endTimeStamp = new Date().toJSON().slice(0, -1);
    let scheduleName: string;
    if (this.measurementControl.schedules && this.measurementControl.schedules.length > 0) {
      scheduleName = this.measurementControl.schedules[0].name;
    }
    const lmapReport = new LmapReport();
    lmapReport.date = endTimeStamp;
    lmapReport.result = [new LmapResult()];
    lmapReport.result[0].schedule = scheduleName;
    lmapReport.result[0].results = [];
    lmapReport.result[0].event = this.startTimeStamp;
    lmapReport.result[0].start = this.startTimeStamp;
    lmapReport.result[0].end = endTimeStamp;

    this.testResults.forEach((subMeasurementResult: SpeedMeasurementResult | QoSMeasurementResult) => {
      lmapReport.result[0].results.push(subMeasurementResult);
    });

    lmapReport.time_based_result = new TimeBasedResultAPI();
    const networkPointInTime: MeasurementResultNetworkPointInTimeAPI = new MeasurementResultNetworkPointInTimeAPI();
    networkPointInTime.network_type_id = this.webNetworkType;
    networkPointInTime.time = endTimeStamp;
    lmapReport.time_based_result.start_time = this.startTimeStamp;
    lmapReport.time_based_result.end_time = endTimeStamp;
    lmapReport.time_based_result.network_points_in_time = [];
    lmapReport.time_based_result.network_points_in_time.push(networkPointInTime);

    this.locationService.stopTracking();
    lmapReport.time_based_result.geo_locations = this.locationService.getLocations();

    this.testService
      .postMeasurementResults(
        lmapReport,
        this.speedControl.option.reduce(
          (url: string, option: LmapOption) => (option.name === 'result_collector_base_url' ? url + option.value : url),
          ''
        )
      )
      .subscribe(response => {
        if (response.data.uuid !== null && response.data.uuid !== '') {
          if (this.zone) {
            this.zone.run(() => {
              this.measurementLink = '/history/' + response.data.uuid;
            });
          }
        }
      });

    this.testResults = [];
  }
}
