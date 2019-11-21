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

  public loopModeConfig: PortBlockingTestConfig = {
    numRepetitions: LoopSettingsImplementation.BASE_CONFIG.numRepetitions,
    timeBetweenRepetitions: LoopSettingsImplementation.BASE_CONFIG.timeBetweenRepetitions
  };

  public curRepetitions = 0;
  public loopModeDelay = 0;
  public timeLeftString = "";

  constructor(testSchedulerService: TestSchedulerService, private zone: NgZone) {
    // TODO: Add missing services
    super(testSchedulerService);
  }

  public start = (config: PortBlockingTestConfig, $state: Subject<LoopSettingsTestState>): void => {
    this.$state = $state;

    const extendedConfig = LoopSettingsImplementation.BASE_CONFIG;
    const state = this.generateInitState(config);

    console.log("curRepetitions: " + this.curRepetitions);
    setTimeout(() => {
      state.basicState = BasicTestState.ENDED;
      this.curRepetitions++;
      this.$state.next(state);
    }, 500);
  };

  protected generateInitState = (config: PortBlockingTestConfig) => {
    const state: LoopSettingsTestState = new LoopSettingsTestState();
    state.basicState = BasicTestState.INITIALIZED;
    return state;
  };

  protected clean = (): void => {
    this.$state = null;
  };

  public onDestroy(): void {
    this.curRepetitions = 0;
    this.loopModeDelay = 0;
    this.timeLeftString = "";
  }
}
