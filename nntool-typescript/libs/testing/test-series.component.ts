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

import {
  AfterViewInit,
  Component,
  ContentChildren,
  EventEmitter,
  Input,
  Output,
  QueryList,
  AfterContentInit,
  ChangeDetectorRef
} from '@angular/core';
import { TestComponentStatus } from './enums/test-component-status.enum';
import { StartableTest } from './test.component';

@Component({
  template: '<ng-content></ng-content>',
  selector: 'app-test-series'
})
export class TestSeriesComponent implements AfterViewInit {
  @ContentChildren('startableTest') public testComponents: QueryList<StartableTest>;

  @Input()
  private autostart = false;

  @Input()
  private visible = true;

  @Output()
  private statusChange: EventEmitter<TestComponentStatus> = new EventEmitter<TestComponentStatus>();

  // for now
  private currentFixedTestComponents: StartableTest[];
  private currentTestIndex = 0;

  constructor() {}

  public ngAfterViewInit(): void {
    /*this.testComponents.changes.subscribe(
            (testComponents) => {
                const test = this.currentFixedTestComponents;
                console.log(test);
            }
        );*/
    setTimeout(() => {
      this.currentFixedTestComponents = this.testComponents.toArray();
      this.currentFixedTestComponents.forEach((testComponent: StartableTest) => {
        testComponent.setActive(false);
        testComponent.onChangedRunningState.subscribe(this.testHasEnded);
      });
      if (this.currentFixedTestComponents.length > 0) {
        this.currentFixedTestComponents[this.currentTestIndex].setActive(true);
        if (this.autostart) {
          this.currentFixedTestComponents[this.currentTestIndex].requestStart();
        }
      }
    }, 0);
  }

  private testHasEnded = (testComponentStatus: TestComponentStatus) => {
    switch (testComponentStatus) {
      case TestComponentStatus.WAITING:
        this.currentFixedTestComponents[this.currentTestIndex].setActive(false);
        this.currentTestIndex = (this.currentTestIndex + 1) % this.currentFixedTestComponents.length;
        this.currentFixedTestComponents[this.currentTestIndex].setActive(true);
        if (this.currentTestIndex !== 0) {
          this.currentFixedTestComponents[this.currentTestIndex].requestStart();
        } else {
          this.statusChange.emit(TestComponentStatus.WAITING);
          this.currentFixedTestComponents[this.currentTestIndex].onOnlyReInit();
        }
        break;
      case TestComponentStatus.WORKING:
        if (this.currentTestIndex === 0) {
          this.statusChange.emit(TestComponentStatus.WORKING);
        }
        break;
      case TestComponentStatus.REQUESTS_CONFIG:
        this.statusChange.emit(TestComponentStatus.REQUESTS_CONFIG);
        break;
    }
  };
}
