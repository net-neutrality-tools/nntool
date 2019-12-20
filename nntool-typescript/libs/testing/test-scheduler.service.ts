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

import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { TestConfig } from './tests-implementation/test-config';
import { TestImplementation } from './tests-implementation/test-implementation';
import { TestState } from './tests-implementation/test-state';

@Injectable({
  providedIn: 'root'
})
export class TestSchedulerService {
  private current: {
    test: TestImplementation<TestConfig, TestState>;
    config: TestConfig;
    $state: Subject<TestState>;
  } = null;
  private impending: Array<{
    test: TestImplementation<TestConfig, TestState>;
    config: TestConfig;
    $state: Subject<TestState>;
  }> = [];

  constructor() {}

  public register = (
    test: TestImplementation<TestConfig, TestState>,
    config: TestConfig,
    $state: Subject<TestState>
  ) => {
    this.impending.push({ test, config, $state });
    if (this.impending.length === 1 && this.current === null) {
      this.startNextTest();
    }
  };

  public reportEnd = (test: TestImplementation<TestConfig, TestState>) => {
    if (this.current.test === test) {
      this.current = null;
      this.startNextTest();
    }
  };

  private startNextTest = () => {
    if (this.impending.length > 0 && this.current === null) {
      this.current = this.impending.shift();
      this.current.test.start(this.current.config, this.current.$state);
    }
  };
}
