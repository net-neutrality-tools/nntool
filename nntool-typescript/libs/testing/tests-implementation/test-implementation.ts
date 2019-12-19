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

import { Subject, Subscription } from 'rxjs';
import { BasicTestState } from '../enums/basic-test-state.enum';
import { TestSchedulerService } from '../test-scheduler.service';
import { TestConfig } from './test-config';
import { TestState } from './test-state';

export abstract class TestImplementation<TC extends TestConfig, TS extends TestState> {
  public abstract start: (config: TC, $state: Subject<TS>) => void;
  public onDestroy() : void {}

  protected abstract generateInitState: (config: TC) => TS;
  protected abstract clean: () => void;

  private stateSubscription: Subscription;

  protected constructor(private testSchedulerService: TestSchedulerService) {}

  public register = (config: TC, $state: Subject<TS>): void => {
    this.stateSubscription = $state.asObservable().subscribe(this.end);
    $state.next(this.generateInitState(config));
    this.testSchedulerService.register(this, config, $state);
  };

  public end = (state: TS): void => {
    if (state.basicState === BasicTestState.ENDED && this.stateSubscription) {
      this.stateSubscription.unsubscribe();
      this.clean();
      this.testSchedulerService.reportEnd(this);
    }
  };
}
