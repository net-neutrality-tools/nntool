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

import { Component, ElementRef, NgZone, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';
import { DeviceDetectorService } from 'ngx-device-detector';
import { TestComponentStatus } from '../testing/enums/test-component-status.enum';
import { PortBlockingTestState } from '../testing/tests-implementation/port-blocking/port-blocking-test-state';
import { SpeedTestState } from '../testing/tests-implementation/speed/speed-test-state';
import { BaseNetTestComponent } from './base_test.component';
import { MeasurementSettings } from './models/measurement-settings';
import { MeasurementResultNetworkPointInTimeAPI } from './models/measurements/measurement-result-network-point-in-time.api';
import { TimeBasedResultAPI } from './models/measurements/time-based-result.api';
import { ServerSelectionComponent } from './test.server_selection';
import { SpeedMeasurementPeer } from './models/server-selection/speed-measurement-peer';
import { MeasurementTypeParameters, LmapOption } from '../core/models/lmap/models/shared/lmap-option.model';
import { LmapControl } from '../core/models/lmap/models/lmap-control.model';
import { LmapTask } from '../core/models/lmap/models/lmap-control/lmap-task.model';
import { SpeedMeasurementResult } from '../core/models/lmap/models/lmap-report/lmap-result/extensions/speed-measurement-result.model';
import { QoSMeasurementResult } from '../core/models/lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';
import { ConnectionInfo } from '../core/models/lmap/models/lmap-report/lmap-result/extensions/connection-info.model';
import { RttInfo } from '../core/models/lmap/models/lmap-report/lmap-result/extensions/rtt-info.model';
import { LmapReport } from '../core/models/lmap/models/lmap-report.model';
import { LmapResult } from '../core/models/lmap/models/lmap-report/lmap-result.model';
import { TestService } from '../core/services/test/test.service';
import { ConfigService } from '../core/services/config.service';
import { UserService } from '../core/services/user.service';
import { RequestsService } from '../core/services/requests.service';
import { AppService } from '../core/services/app.service';
import { LocationService } from '../core/services/location.service';
import { PointInTimeValueAPI } from './models/measurements/point-in-time-value.api';
import { WebSocketInfo } from '@nntool-typescript/core/models/lmap/models/lmap-report/lmap-result/extensions/web-socket-info.model';
import { NumStreamsInfo } from '@nntool-typescript/core/models/lmap/models/lmap-report/lmap-result/extensions/num-streams-info.model';
import { TracerouteTestState } from '@nntool-typescript/testing/tests-implementation/traceroute/traceroute-test-state';
import { BasicTestState } from '@nntool-typescript/testing/enums/basic-test-state.enum';
import { isElectron } from '@nntool-typescript/utils';

export { TestGuard } from './test.guard';

@Component({
  templateUrl: './test.component.html'
})
export class NetTestComponent extends BaseNetTestComponent implements OnInit {
  @ViewChild(ServerSelectionComponent, { static: false }) public serverSelectionComponent: ServerSelectionComponent;
  public speedConfig: MeasurementSettings = undefined; // TODO: change to measurement configuration
  public qosConfig: MeasurementTypeParameters = undefined; // TODO: change to measurement configuration
  public runningInElectron = isElectron();

  protected measurementControl: LmapControl = undefined;

  private readonly webNetworkType: number = 95;
  private readonly paramSpeed: string = 'parameters_speed';
  private readonly paramServerPort: string = 'server_port';
  private readonly paramServerAddress: string = 'server_addr_default';
  private readonly paramCollector: string = 'result_collector_base_url';
  private readonly paramEncryption: string = 'encryption';
  private speedControl: LmapTask = undefined;
  private qosControl: LmapTask = undefined;
  private testResults: Array<SpeedMeasurementResult | QoSMeasurementResult> = [];
  private startTimeStamp: string = undefined;
  private selectedSpeedMeasurementPeer: SpeedMeasurementPeer = undefined;

  private network_info?: any;
  private system_usage_info?: any;

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
    speedMeasurementResult.status = null;
    speedMeasurementResult.rtt_info = new RttInfo();
    speedMeasurementResult.connection_info = new ConnectionInfo();
    speedMeasurementResult.connection_info.identifier = this.selectedSpeedMeasurementPeer.identifier;
    speedMeasurementResult.connection_info.num_streams_info = new NumStreamsInfo();

    if (typeof speedTestResult.completeTestResult !== 'undefined') {
      const completeResult = speedTestResult.completeTestResult;
      //console.log(completeResult);

      speedMeasurementResult.relative_start_time_ns = completeResult.time_info.measurement_start;
      speedMeasurementResult.relative_end_time_ns = completeResult.time_info.measurement_end;

      speedMeasurementResult.connection_info.address = completeResult.peer_info.url;
      speedMeasurementResult.connection_info.port = completeResult.peer_info.port;
      speedMeasurementResult.connection_info.encrypted = completeResult.peer_info.tls === '1';

      // RTT
      if (completeResult.rtt_info !== undefined) {
        const currentRtt = completeResult.rtt_info;
        speedMeasurementResult.duration_rtt_ns = currentRtt.duration_ns;
        speedMeasurementResult.rtt_info = {
          rtts: currentRtt.rtts.map(item => {
            return { rtt_ns: item.rtt_ns, relative_time_ns: item.relative_time_ns_measurement_start };
          }),
          requested_num_packets: 10, // TODO: fill w/server setting
          num_sent: currentRtt.num_sent,
          num_received: currentRtt.num_received,
          num_error: currentRtt.num_error,
          num_missing: currentRtt.num_missing,
          packet_size: currentRtt.packet_size,
          average_ns: currentRtt.average_ns,
          maximum_ns: currentRtt.max_ns,
          median_ns: currentRtt.median_ns,
          minimum_ns: currentRtt.min_ns,
          standard_deviation_ns: currentRtt.standard_deviation_ns
        };
      }

      // download
      if (completeResult.download_info !== undefined && completeResult.download_info.length > 0) {
        const currentDownload = completeResult.download_info[completeResult.download_info.length - 1];
        // we sent the duration (not the duration_total), as the mbps are calculated from the duration => the total duration can be calculated from the relative starttimes
        speedMeasurementResult.duration_download_ns = currentDownload.duration_ns;
        speedMeasurementResult.bytes_download = currentDownload.bytes;
        speedMeasurementResult.bytes_download_including_slow_start = currentDownload.bytes_including_slow_start;

        speedMeasurementResult.connection_info.num_streams_info.requested_num_streams_download =
          currentDownload.num_streams_start;
        speedMeasurementResult.connection_info.num_streams_info.actual_num_streams_download =
          currentDownload.num_streams_end;

        const wsInfoDl = new WebSocketInfo();

        wsInfoDl.frame_count = currentDownload.frame_count;
        wsInfoDl.frame_size = currentDownload.frame_size;
        wsInfoDl.frame_count_including_slow_start = currentDownload.frame_count_including_slow_start;
        wsInfoDl.overhead = currentDownload.overhead;
        wsInfoDl.overhead_per_frame = currentDownload.overhead_per_frame;
        wsInfoDl.overhead_per_frame_including_slow_start = currentDownload.overhead_per_frame_including_slow_start;

        speedMeasurementResult.connection_info.web_socket_info_download = wsInfoDl;
      }

      // upload
      if (completeResult.upload_info !== undefined && completeResult.upload_info.length > 0) {
        const currentUpload = completeResult.upload_info[completeResult.upload_info.length - 1];
        // we sent the duration (not the duration_total), as the mbps are calculated from the duration => the total duration can be calculated from the relative starttimes
        speedMeasurementResult.duration_upload_ns = currentUpload.duration_ns;
        speedMeasurementResult.bytes_upload = currentUpload.bytes;
        speedMeasurementResult.bytes_upload_including_slow_start = currentUpload.bytes_including_slow_start;

        speedMeasurementResult.connection_info.num_streams_info.requested_num_streams_upload =
          currentUpload.num_streams_start;
        speedMeasurementResult.connection_info.num_streams_info.actual_num_streams_upload =
          currentUpload.num_streams_end;

        const wsInfoUl = new WebSocketInfo();

        wsInfoUl.frame_count = currentUpload.frame_count;
        wsInfoUl.frame_size = currentUpload.frame_size;
        wsInfoUl.frame_count_including_slow_start = currentUpload.frame_count_including_slow_start;
        wsInfoUl.overhead = currentUpload.overhead;
        wsInfoUl.overhead_per_frame = currentUpload.overhead_per_frame;
        wsInfoUl.overhead_per_frame_including_slow_start = currentUpload.overhead_per_frame_including_slow_start;

        speedMeasurementResult.connection_info.web_socket_info_upload = wsInfoUl;
      }

      if (completeResult.client_info.type === 'DESKTOP') {
        this.network_info = completeResult.network_info;
        this.system_usage_info = completeResult.system_usage_info;
      }
    }

    this.testResults.push(speedMeasurementResult);
  }

  public portBlockingTestFinished(portBlockingTestResult: PortBlockingTestState): void {
    const qosMeasurementResult: QoSMeasurementResult = this.getCurrentQoSResult();

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
    
  }

  public tracerouteTestFinished(tracerouteTestResult: TracerouteTestState): void {
    if (!tracerouteTestResult.result || !tracerouteTestResult.result.is_reverse) {
      return;
    }
    const qosResult: QoSMeasurementResult = this.getCurrentQoSResult();

    let traceroute_result_details: Array<any> = new Array();
    if (tracerouteTestResult.result && tracerouteTestResult.result.hops) {
      for (const hop of tracerouteTestResult.result.hops) {
        traceroute_result_details.push({
          host: hop.ip,
        })
      }
    }

    qosResult.results.push({
      traceroute_result_details: traceroute_result_details,
      traceroute_result_status:  traceroute_result_details.length > 0 ? "OK" : "ERROR",
      duration_ns: !tracerouteTestResult.result.startTimeNs ? undefined : !tracerouteTestResult.result.endTimeNs ? undefined : tracerouteTestResult.result.endTimeNs - tracerouteTestResult.result.startTimeNs,
      test_type: "traceroute",
      traceroute_objective_max_hops: tracerouteTestResult.result.max_hops,
      traceroute_objective_timeout: tracerouteTestResult.result.timeout,
      traceroute_objective_is_reverse: tracerouteTestResult.result.is_reverse,
      qos_test_uid: tracerouteTestResult.result.qos_test_uid,
      traceroute_objective_host: tracerouteTestResult.result.host,
      traceroute_objective_port: tracerouteTestResult.result.port,
      traceroute_result_hops: traceroute_result_details.length,
    });
    
  }

  /**
   * Returns a QoSMeasurementResult to which any new results shall be appended to
   * (either creates a new one or, if one was already created, returns the previously created one)
   */
  private getCurrentQoSResult(): QoSMeasurementResult {

    let qosResult: QoSMeasurementResult = undefined;
    if (this.testResults.length > 0) {
      for (let singleTest of this.testResults) {
        if (singleTest instanceof QoSMeasurementResult) {
          qosResult = singleTest;
        }
      }
    }
     
    if (!qosResult) {
      qosResult = new QoSMeasurementResult();
      qosResult.deserialize_type = 'qos_result';
      qosResult.reason = null;
      qosResult.relative_end_time_ns = null;
      qosResult.relative_start_time_ns = null;
      qosResult.status = null;
      qosResult.results = [];
      this.testResults.push(qosResult);
    }
  
    return qosResult;
  }

  private requestMeasurement(): void {
    this.selectedSpeedMeasurementPeer = this.serverSelectionComponent.getCurrentSpeedServer();
    this.testService.newMeasurement(undefined, this.selectedSpeedMeasurementPeer).subscribe(response => {
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
            for (const option of task.option) {
              switch (option.name) {
                case this.paramCollector:
                  this.speedConfig.collectorAddress = option.value;
                  break;
                case this.paramServerAddress:
                  this.speedConfig.serverAddress = option.value;
                  break;
                case this.paramServerPort:
                  this.speedConfig.serverPort = option.value;
                  break;
                case this.paramSpeed: {
                  /* tslint:disable:no-string-literal */
                  this.speedConfig.speedConfig = option['measurement-parameters'].measurement_configuration;
                  /* tslint:enable:no-string-literal */
                  break;
                }
                case this.paramEncryption:
                  this.speedConfig.encryption = option.value === 'true';
                  break;
              }
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
    const networkPointInTime = new MeasurementResultNetworkPointInTimeAPI();
    networkPointInTime.network_type_id = this.webNetworkType;
    networkPointInTime.time = endTimeStamp;
    lmapReport.time_based_result.start_time = this.startTimeStamp;
    lmapReport.time_based_result.end_time = endTimeStamp;
    lmapReport.time_based_result.network_points_in_time = [];
    lmapReport.time_based_result.network_points_in_time.push(networkPointInTime);

    this.locationService.stopTracking();
    lmapReport.time_based_result.geo_locations = this.locationService.getLocations();

    if (this.network_info) {
      const npit_info = lmapReport.time_based_result.network_points_in_time[0];

      if (typeof this.network_info.dsk_lan_detected !== 'undefined')
      {
        if (this.network_info.dsk_lan_detected)
        {
          // LAN
          npit_info.network_type_id = 98;
        } else {
          // WLAN
          npit_info.network_type_id = 99;
        }
      }
      else
      {
        // UNKNOWN
        npit_info.network_type_id = 95;
      }
    }

    if (this.system_usage_info) {
      lmapReport.time_based_result.cpu_usage = [
        new PointInTimeValueAPI<number>(this.system_usage_info.dsk_cpu_load_avg, 0)
      ];
      lmapReport.time_based_result.mem_usage = [
        new PointInTimeValueAPI<number>(this.system_usage_info.dsk_mem_load_avg, 0)
      ];
    }

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
