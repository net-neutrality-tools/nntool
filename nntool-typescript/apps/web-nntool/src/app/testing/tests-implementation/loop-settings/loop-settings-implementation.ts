import { Injectable, NgZone } from '@angular/core';
import { Subject } from 'rxjs';
import { BasicTestState } from '../../enums/basic-test-state.enum';
import { TestSchedulerService } from '../../test-scheduler.service';
import { TestImplementation } from '../test-implementation';
import { PortBlockingTestConfig } from '../port-blocking/port-blocking-test-config';
import { LoopSettingsTestState } from './loop-settings-test-state';

declare var PortBlocking: any;

@Injectable({
  providedIn: 'root'
})
export class LoopSettingsImplementation extends TestImplementation<PortBlockingTestConfig, LoopSettingsTestState> {
  private static BASE_CONFIG: PortBlockingTestConfig = {
    numRepetitions: 5,
    timeBetweenRepetitions: 10
  };

  private $state: Subject<LoopSettingsTestState>;

  constructor(testSchedulerService: TestSchedulerService, private zone: NgZone) {
    // TODO: Add missing services
    super(testSchedulerService);
  }

  public start = (config: PortBlockingTestConfig, $state: Subject<LoopSettingsTestState>): void => {
    this.$state = $state;

    const extendedConfig = LoopSettingsImplementation.BASE_CONFIG;
    const state = this.generateInitState(config);

   setTimeout(state.basicState = BasicTestState.ENDED, 3000);
  };

  protected generateInitState = (config: PortBlockingTestConfig) => {

    const state: LoopSettingsTestState = new LoopSettingsTestState();

    state.basicState = BasicTestState.INITIALIZED;

    return state;
  };

  protected clean = (): void => {
    this.$state = null;
  };
}
