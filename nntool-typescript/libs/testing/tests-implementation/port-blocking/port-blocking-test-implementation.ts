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
import { Subject } from 'rxjs';
import { BasicTestState } from '../../enums/basic-test-state.enum';
import { TestSchedulerService } from '../../test-scheduler.service';
import { TestImplementation } from '../test-implementation';
import { PortBlockingTestTypeEnum } from './enums/port-blocking-test-type';
import { PortBlockingTestConfig } from './port-blocking-test-config';
import { PortBlockingTestState } from './port-blocking-test-state';

declare var UdpPortBlocking: any;

@Injectable({
  providedIn: 'root'
})
export class PortBlockingTestImplementation extends TestImplementation<PortBlockingTestConfig, PortBlockingTestState> {
  private static BASE_CONFIG: PortBlockingTestConfig = {
    platform: 'web',
    ports: []
  };

  private $state: Subject<PortBlockingTestState>;
  private portBlocking: any;

  constructor(testSchedulerService: TestSchedulerService, private zone: NgZone) {
    // TODO: Add missing services
    super(testSchedulerService);
  }

  public start = (config: PortBlockingTestConfig, $state: Subject<PortBlockingTestState>): void => {
    this.$state = $state;

    if (!config.UDP_TURN || config.UDP_TURN.size == 0) {
      const state = this.generateInitState(config);
      state.basicState = BasicTestState.ENDED;
      this.$state.next(state);
      return;
    }

    const extendedConfig = PortBlockingTestImplementation.BASE_CONFIG;

    extendedConfig.target = config.UDP_TURN[0].target;
    extendedConfig.targetIpv4 = config.UDP_TURN[0].target_ipv4;
    extendedConfig.targetIpv6 = config.UDP_TURN[0].target_ipv6;
    extendedConfig.user = config.UDP_TURN[0].user;
    extendedConfig.password = config.UDP_TURN[0].password;
    extendedConfig.timeout = config.UDP_TURN[0].timeout / 1000_000;

    config.UDP_TURN.forEach(elem => {
        extendedConfig.ports.push({
          port: elem.port,
          packets: elem.num_packets
        });
    });

    this.portBlocking = new UdpPortBlocking();
    this.zone.runOutsideAngular(() => {
      const state = this.generateInitState(config);

      (window as any).measurementCallback = (data: any) => {
        // TODO: inject window object properly
        if (!this.$state) {
          return;
        }
        const currentState = JSON.parse(data);
        switch (currentState.cmd) {
          case 'report': {
            state.basicState = BasicTestState.RUNNING;
            break;
          }
          case 'completed': {
            state.basicState = BasicTestState.ENDED;
            break;
          }
        }
        if (currentState.udp_port_blocking && currentState.udp_port_blocking.results) {
          currentState.udp_port_blocking.results.forEach((result: any) => {
            const testedPort = parseInt(result.port, 10);
            state.types[0].ports.forEach((port: { number: number; packets: { requested_packets: number; lost: number; received: number; sent:number}; delay: {average_ns: number; standard_deviation_ns: number}; delays: number[]; uid: string }) => {
              if (port.number === testedPort) {
                port.packets.lost = result.packets.lost;
                port.packets.received = result.packets.received;
                port.packets.sent = result.packets.sent;
                port.delay = result.delay;
                port.delays = result.delays;
              }
            });
          });
        }
        this.$state.next(state);
      };
      this.$state.next(state);
    });
    this.portBlocking.measurementStart(extendedConfig);
  };

  protected generateInitState = (config: PortBlockingTestConfig) => {
    const state: PortBlockingTestState = new PortBlockingTestState();

    state.basicState = BasicTestState.INITIALIZED;
    state.types = [
      {
        key: PortBlockingTestTypeEnum.UDP_TURN,
        ports: []
      }
    ];

    if (config.UDP_TURN) {
      const portInformation: Array<{ port: number; uid: string; packets_requested: number }> = config.UDP_TURN.map((settings: any) => ({
        port: parseInt(settings.port, 10),
        uid: settings.qos_test_uid,
        packets_requested: parseInt(settings.num_packets, 10) 
      }));

      portInformation.forEach((port: { port: number; uid: string; packets_requested: number }) => {
        state.types[0].ports.push({
          number: port.port,
          packets: {
            requested_packets: port.packets_requested,
            lost: 0,
            sent: 0,
            received: 0
          },
          uid: port.uid
        });
      });
    }

    return state;
  };

  protected clean = (): void => {
    this.$state = null;
  };
}
