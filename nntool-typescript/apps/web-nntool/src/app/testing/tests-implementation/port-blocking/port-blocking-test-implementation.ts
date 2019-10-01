import { Injectable, NgZone } from '@angular/core';
import { Subject } from 'rxjs';
import { BasicTestState } from '../../enums/basic-test-state.enum';
import { TestSchedulerService } from '../../test-scheduler.service';
import { TestImplementation } from '../test-implementation';
import { PortBlockingTestTypeEnum } from './enums/port-blocking-test-type';
import { PortBlockingTestConfig } from './port-blocking-test-config';
import { PortBlockingTestState } from './port-blocking-test-state';

declare var PortBlocking: any;

@Injectable({
  providedIn: 'root'
})
export class PortBlockingTestImplementation extends TestImplementation<PortBlockingTestConfig, PortBlockingTestState> {
  private static BASE_CONFIG: PortBlockingTestConfig = {
    platform: 'web',
    target: 'peer-ias-de-01.net-neutrality.tools',
    targetIpv4: '82.199.148.52',
    targetIpv6: '2a01:4a0:f::11',
    user: 'berec',
    password: 'berec',
    ports: [123, 500, 4500, 5060, 7000],
    timeout: 5000
  };

  private $state: Subject<PortBlockingTestState>;
  private portBlocking: any;

  constructor(testSchedulerService: TestSchedulerService, private zone: NgZone) {
    // TODO: Add missing services
    super(testSchedulerService);
  }

  public start = (config: PortBlockingTestConfig, $state: Subject<PortBlockingTestState>): void => {
    this.$state = $state;

    const extendedConfig = PortBlockingTestImplementation.BASE_CONFIG;
    extendedConfig.ports = config.UDP.map((settings: any) => parseInt(settings.out_port, 10));

    this.portBlocking = new PortBlocking();
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
        if (currentState.port_blocking && currentState.port_blocking.results) {
          currentState.port_blocking.results.forEach((result: any) => {
            const testedPort = result.port;
            const reachable = result.reachable;
            const finished = result.reachable || result.timeout;
            state.types[0].ports.forEach((port: { number: number; reachable: false; finished: false; uid: string }) => {
              if (port.number === testedPort) {
                port.reachable = reachable;
                port.finished = finished;
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
    const portInformation: Array<{ port: number; uid: string }> = config.UDP.map((settings: any) => ({
      port: parseInt(settings.out_port, 10),
      uid: settings.qos_test_uid
    }));

    const state: PortBlockingTestState = new PortBlockingTestState();

    state.basicState = BasicTestState.INITIALIZED;
    state.types = [
      {
        key: PortBlockingTestTypeEnum.UDP,
        ports: []
      }
    ];

    portInformation.forEach((port: { port: number; uid: string }) => {
      state.types[0].ports.push({
        number: port.port,
        reachable: false,
        finished: false,
        uid: port.uid
      });
    });
    return state;
  };

  protected clean = (): void => {
    this.$state = null;
  };
}
