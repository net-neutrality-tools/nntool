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

import { Injectable, NgZone } from '@angular/core';
import { Subject, config } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { MeasurementSettings } from '../../../test/models/measurement-settings';
import { BasicTestState } from '../../enums/basic-test-state.enum';
import { TestSchedulerService } from '../../test-scheduler.service';
import { TestImplementation } from '../test-implementation';
import { SpeedTestStateEnum } from './enums/speed-test-state.enum';
import { SpeedTestConfig } from './speed-test-config';
import { SpeedTestState } from './speed-test-state';
import { UserService } from '../../../core/services/user.service';
import { IasService } from '@nntool-typescript/ias/ias.service';
import { isElectron } from '@nntool-typescript/utils';

@Injectable({
  providedIn: 'root'
})
export class SpeedTestImplementation extends TestImplementation<SpeedTestConfig, SpeedTestState> {
  private $state: Subject<SpeedTestState>;

  constructor(
    private logger: NGXLogger,
    testSchedulerService: TestSchedulerService,
    private zone: NgZone,
    private userService: UserService,
    private iasService: IasService
  ) {
    // TODO: Add missing services
    super(testSchedulerService);
  }

  public start = (config: MeasurementSettings, $state: Subject<SpeedTestState>): void => {
    if (this.iasService.isRunning()) {
      return;
    }

    this.$state = $state;

    const rttRequests = 10;
    const rttRequestTimeout = 2000;
    const rttRequestWait = 500;
    const rttDuration = rttRequests * (rttRequestTimeout + rttRequestWait) * 1.1;
    const downloadUploadDuration = 10000;

    const firstDotIndex = config.serverAddress.indexOf('.');

    const extendedConfig = {
      cmd: 'start',
      platform: this.iasService.getPlatform(),
      wsTargets: [config.serverAddress.substr(0, firstDotIndex)],
      wsTLD: config.serverAddress.substr(firstDotIndex + 1),
      wsTargetPort: config.serverPort,
      wsWss: config.encryption ? 1 : 0,
      wsAuthToken: 'placeholderToken',
      wsAuthTimestamp: 'placeholderTimestamp',
      performRouteToClientLookup: false,
      rtt: {
        performMeasurement: this.userService.user.executePingMeasurement
      },
      download: {
        performMeasurement:
          this.userService.user.executeDownloadMeasurement &&
          config.speedConfig.download !== undefined &&
          config.speedConfig.download !== null,
        classes: config.speedConfig.download
      },
      upload: {
        performMeasurement:
          this.userService.user.executeUploadMeasurement &&
          config.speedConfig.upload !== undefined &&
          config.speedConfig.upload !== null,
        classes: config.speedConfig.upload
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
      this.iasService.start(extendedConfig);
    });
  };

  protected generateInitState = () => {
    const state = new SpeedTestState();

    state.basicState = BasicTestState.INITIALIZED;
    state.speedTestState = SpeedTestStateEnum.READY;
    state.uuid = null;
    state.serverName = null;
    state.remoteIp = null;
    state.provider = null;
    state.location = null;
    state.device = null;
    state.technology = null;

    return state;
  };

  protected clean = (): void => {
    this.iasService.stop();
    this.$state = null;
  };

  private setupCallback = (config: SpeedTestConfig): void => {
    const state = this.generateInitState();

    this.iasService.setCallback((data: any) => {
      // TODO: inject window object properly
      if (!this.$state) {
        return;
      }

      const currentState = JSON.parse(data);

      state.device = currentState.device_info.os_info.name + ' ' + currentState.device_info.os_info.version;

      //show os and connection on DESKTOP, browser and browser version on BROWSER
      if (isElectron()) {
        if (currentState.network_info) {
          if (typeof currentState.network_info.dsk_lan_detected !== 'undefined') {
            if (currentState.network_info.dsk_lan_detected) {
              // LAN
              state.technology = 'LAN';
            } else {
              // WLAN
              state.technology = 'WLAN';
            }
          } else {
            // UNKNOWN
            state.technology = 'UNKNOWN';
          }
        }
      } else {
        state.technology = currentState.device_info.browser_info.name.split(' ')[0] + ' ' + currentState.device_info.browser_info.version;
      }

      if (typeof currentState.peer_info !== 'undefined' && typeof currentState.peer_info.url !== 'undefined') {
        state.serverName = currentState.peer_info.url;
      }

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
            if (currentState.download_info) {
              currentDownload = currentState.download_info[currentState.download_info.length - 1];
              state.downBit = currentDownload.throughput_avg_bps;
              state.downMBit = state.downBit / (1000 * 1000);
              state.progress = currentDownload.duration_ns / (1000.0 * 1000.0) / config.wsMeasureTime;
            }
          }
          if (currentState.cmd === 'finish') {
            currentDownload = currentState.download_info[currentState.download_info.length - 1];
            state.speedTestState = SpeedTestStateEnum.DOWN_OK;
            state.downBit = currentDownload.throughput_avg_bps;
            state.downMBit = state.downBit / (1000 * 1000);
            state.progress = 1;
            this.zone.run(() => this.$state.next(state));
            // this.testGauge.onStateChange(state);
            state.speedTestState = SpeedTestStateEnum.UP_PRE;
            state.progress = 0;
            this.zone.run(() => this.$state.next(state));
            // this.testGauge.onStateChange(state);
            state.speedTestState = SpeedTestStateEnum.UP_PRE_OK;
            state.progress = 0;
          }
          break;
        case 'upload':
          let currentUpload;
          if (currentState.cmd === 'report') {
            state.speedTestState = SpeedTestStateEnum.UP;
            if (currentState.upload_info) {
              currentUpload = currentState.upload_info[currentState.upload_info.length - 1];
              state.upMBit = currentUpload.throughput_avg_bps / (1000 * 1000);
              state.upBit = currentUpload.throughput_avg_bps;
              state.progress = currentUpload.duration_ns / (1000 * 1000) / config.wsMeasureTime;
            }
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

            //this.logger.debug('Mes uuids', data);
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

        this.iasService.stop();
      }
      this.zone.run(() => this.$state.next(state));
      // this.testGauge.onStateChange(state);
    });

    // this.testGauge.onStateChange(state);
    this.zone.run(() => this.$state.next(state));
  };
}
