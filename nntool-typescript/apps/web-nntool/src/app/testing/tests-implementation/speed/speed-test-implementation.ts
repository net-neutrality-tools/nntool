import { Injectable, NgZone } from '@angular/core';
import { Subject } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { MeasurementSettings } from '../../../test/models/measurement-settings';
import { BasicTestState } from '../../enums/basic-test-state.enum';
import { TestSchedulerService } from '../../test-scheduler.service';
import { TestImplementation } from '../test-implementation';
import { SpeedTestStateEnum } from './enums/speed-test-state.enum';
import { SpeedTestConfig } from './speed-test-config';
import { SpeedTestState } from './speed-test-state';
import { UserService } from '../../../@core/services/user.service';

declare var Ias: any;

@Injectable({
  providedIn: 'root'
})
export class SpeedTestImplementation extends TestImplementation<SpeedTestConfig, SpeedTestState> {
  private $state: Subject<SpeedTestState>;
  private ias: any = undefined;

  constructor(private logger: NGXLogger, testSchedulerService: TestSchedulerService, private zone: NgZone, private userService: UserService) {
    // TODO: Add missing services
    super(testSchedulerService);
  }

  protected generateInitState = (config: SpeedTestConfig) => {
    const rttRequests = 10;
    const rttRequestTimeout = 2000;
    const rttRequestWait = 500;
    const rttDuration = (rttRequests * (rttRequestTimeout + rttRequestWait)) * 1.1;
    const downloadUploadDuration = 10000;

    config = {
      cmd: 'start',
      platform: 'web',
      wsTargets: ['peer-ias-de-01'],
      wsTLD: 'net-neutrality.tools',
      wsTargetPort: '80',
      wsWss: 0,
      wsAuthToken: 'placeholderToken',
      wsAuthTimestamp: 'placeholderTimestamp',
      performRouteToClientLookup: false,
      performRttMeasurement: this.userService.user.executePingMeasurement,
      performDownloadMeasurement: this.userService.user.executeDownloadMeasurement,
      performUploadMeasurement: this.userService.user.executeUploadMeasurement,
      wsParallelStreamsDownload: 4,
      wsFrameSizeDownload: 32768,
      downloadThroughputLowerBoundMbps: 0.95,
      downloadThroughputUpperBoundMbps: 525,
      wsParallelStreamsUpload: 4,
      wsFrameSizeUpload: 65535,
      uploadThroughputLowerBoundMbps: 0.95,
      uploadThroughputUpperBoundMbps: 525,
      uploadFramesPerCall: 1,
      wsRttRequests: rttRequests,
      wsRttRequestTimeout: rttRequestTimeout,
      wsRttRequestWait: rttRequestWait,
      wsRttTimeout: rttDuration,
      wsMeasureTime: downloadUploadDuration,
      rtt: { performMeasurement: true },
      download: { performMeasurement: true },
      upload: { performMeasurement: true }
    };

    const state: SpeedTestState = new SpeedTestState();
    state.basicState = BasicTestState.INITIALIZED;
    state.speedTestState = SpeedTestStateEnum.READY;
    state.uuid = null;
    state.serverName = null;
    state.remoteIp = null;
    state.provider = null;
    state.location = null;
    state.device = null;
    state.technology = 'BROWSER';
    return state;
  }

  public start = (config: MeasurementSettings, $state: Subject<SpeedTestState>): void => {
    if (this.ias !== undefined) {
      return;
    }

    this.$state = $state;

    const rttRequests = 10;
    const rttRequestTimeout = 2000;
    const rttRequestWait = 500;
    const rttDuration = (rttRequests * (rttRequestTimeout + rttRequestWait)) * 1.1;
    const downloadUploadDuration = 10000;

    const firstDotIndex = config.serverAddress.indexOf('.');

    const extendedConfig = {
      cmd: 'start',
      platform: 'web',
      wsTargets: [config.serverAddress.substr(0, firstDotIndex)],
      wsTLD: config.serverAddress.substr(firstDotIndex + 1),
      wsTargetPort: config.serverPort,
      wsWss: config.encryption ? 1 : 0,
      wsAuthToken: 'placeholderToken',
      wsAuthTimestamp: 'placeholderTimestamp',
      performRouteToClientLookup: true,
      routeToClientTargetPort: 8080,

      rtt: {
        performMeasurement: this.userService.user.executePingMeasurement
      },
      download: {
        performMeasurement: this.userService.user.executeDownloadMeasurement && config.speedConfig.download !== undefined && config.speedConfig.download !== null,
        classes: config.speedConfig.download,
        /*"streams": 4,
        "frameSize": 32768*/
      },
      upload: {
        performMeasurement: this.userService.user.executeUploadMeasurement && config.speedConfig.upload !== undefined && config.speedConfig.upload !== null,
        classes: config.speedConfig.upload
        /*"streams": 4,
                "frameSize": 65535,
                "framesPerCall": 1*/
      },
      wsRttRequests: rttRequests,
      wsRttRequestTimeout: rttRequestTimeout,
      wsRttRequestWait: rttRequestWait,
      wsRttTimeout: rttDuration,
      wsMeasureTime: downloadUploadDuration
    };

    if (window.location.protocol === 'https:') {
      extendedConfig.wsTargetPort = '443';
      extendedConfig.wsWss = 1;
    }

    this.setupCallback(extendedConfig);

    this.zone.runOutsideAngular(() => {  

      this.ias = new Ias();
      this.ias.measurementStart(JSON.stringify(extendedConfig));
    });
  };

  protected clean = (): void => {
    if (this.ias) {
      this.ias.measurementStop();
      this.ias = null;
    }
    this.$state = null;
  };

  private setupCallback = (config: SpeedTestConfig): void => {
    const state = this.generateInitState(config);

    (window as any).iasCallback = (data: any) => {
      // TODO: inject window object properly
      if (!this.$state) {
        return;
      }

      const currentState = JSON.parse(data);
      state.device = currentState.device_info.browser_info.name.split(' ')[0];
      switch (currentState.test_case) {
        case 'rtt':
          if (currentState.cmd === 'report') {
            state.speedTestState = SpeedTestStateEnum.PING;
            state.ping = currentState.rtt_info.average_ns / (1000 * 1000);
            state.progress =
              (currentState.rtt_info.num_received +
                currentState.rtt_info.num_missing +
                currentState.rtt_info.num_error) /
              config.wsRttRequests;
          }
          if (currentState.cmd === 'finish') {
            state.speedTestState = SpeedTestStateEnum.PING_OK;
            state.ping = currentState.rtt_info.average_ns / (1000 * 1000);
            state.progress = 1;
          }
          break;
        case 'download':
          let currentDownload;
          if (currentState.cmd === 'report') {
            state.speedTestState = SpeedTestStateEnum.DOWN;
            currentDownload = currentState.download_info[currentState.download_info.length - 1];
            state.downBit = currentDownload.throughput_avg_bps;
            state.downMBit = state.downBit / (1000 * 1000);
            state.progress = currentDownload.duration_ns / (1000.0 * 1000.0) / config.wsMeasureTime;
          }
          if (currentState.cmd === 'finish') {
            currentDownload = currentState.download_info[currentState.download_info.length - 1];
            state.speedTestState = SpeedTestStateEnum.DOWN_OK;
            state.downBit = currentDownload.throughput_avg_bps;
            state.downMBit = state.downBit / (1000 * 1000);
            state.progress = 1;
            this.zone.run( () => this.$state.next(state));
            // this.testGauge.onStateChange(state);
            state.speedTestState = SpeedTestStateEnum.UP_PRE;
            state.progress = 0;
            this.zone.run( () => this.$state.next(state));
            // this.testGauge.onStateChange(state);
            state.speedTestState = SpeedTestStateEnum.UP_PRE_OK;
            state.progress = 0;
          }
          break;
        case 'upload':
          let currentUpload;
          if (currentState.cmd === 'report') {
            state.speedTestState = SpeedTestStateEnum.UP;
            currentUpload = currentState.upload_info[currentState.upload_info.length - 1];
            state.upMBit = currentUpload.throughput_avg_bps / (1000 * 1000);
            state.upBit = currentUpload.throughput_avg_bps;
            state.progress = currentUpload.duration_ns / (1000 * 1000) / config.wsMeasureTime;
          }
          if (currentState.cmd === 'finish') {
            state.speedTestState = SpeedTestStateEnum.UP_OK;
            currentUpload = currentState.upload_info[currentState.upload_info.length - 1];
            state.upMBit = currentUpload.throughput_avg_bps / (1000 * 1000);
            state.upBit = currentUpload.throughput_avg_bps;
            state.progress = 1;
            this.zone.run(() => this.$state.next(state));
            // this.testGauge.onStateChange(state);
            state.speedTestState = SpeedTestStateEnum.COMPLETE;
            state.progress = 0;

            this.logger.debug('Mes uuids', data);
            // this.testInProgress = false;
          }
          break;
      }
      if (currentState.cmd === 'completed') {
        // only store the full result on completion
        state.completeTestResult = currentState;
        state.speedTestState = SpeedTestStateEnum.COMPLETE;
        state.basicState = BasicTestState.ENDED;
        state.progress = 0;

        this.ias = undefined;
      }
      this.zone.run( () => this.$state.next(state));
      // this.testGauge.onStateChange(state);
    };
    // this.testGauge.onStateChange(state);
    this.zone.run( () =>
      this.$state.next(state)
    );
  };
}
