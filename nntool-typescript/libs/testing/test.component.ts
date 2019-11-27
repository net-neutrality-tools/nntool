import { EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { merge, Observable, Subject } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { BasicTestState } from './enums/basic-test-state.enum';
import { TestComponentStatus } from './enums/test-component-status.enum';
import { TestConfig } from './tests-implementation/test-config';
import { TestImplementation } from './tests-implementation/test-implementation';
import { TestState } from './tests-implementation/test-state';
import { UIState } from './tests-ui/ui-state';

export abstract class StartableTest {
  public requestStart: () => void;
  public onChangedRunningState: Observable<TestComponentStatus>;
  public setActive: (active: boolean) => void;
}

export abstract class Test<
  US extends UIState,
  T extends TestImplementation<TC, TS>,
  TC extends TestConfig,
  TS extends TestState
> extends StartableTest implements OnInit, OnChanges {
  @Input()
  protected config?: TC;

  @Output()
  protected errorMsgChange: EventEmitter<string> = new EventEmitter<string>();
  protected state: Observable<US>;
  public manualStartShouldBeDisabled = false;

  protected abstract testStateToUIState: (state: TS) => US;

  @Output()
  private finished: EventEmitter<TS> = new EventEmitter<TS>();

  @Output()
  private statusChange: EventEmitter<TestComponentStatus> = new EventEmitter<TestComponentStatus>();

  private testImplementation: T;
  private $state: Subject<TS> = new Subject<TS>();

  private $onStatusNotWaiting: Subject<TestComponentStatus.REQUESTS_CONFIG | TestComponentStatus.WORKING> = new Subject<
    TestComponentStatus.REQUESTS_CONFIG | TestComponentStatus.WORKING
  >();
  private onStatusWaiting$: Observable<TestComponentStatus.WAITING> = this.$state.asObservable().pipe(
    filter((state: TS) => {
      return state.basicState === BasicTestState.ENDED && this.status === TestComponentStatus.WORKING;
    }),
    tap((state: TS) => {
      this.status = TestComponentStatus.WAITING;
      this.statusChange.emit(TestComponentStatus.WAITING);
      this.config = undefined;
      this.manualStartShouldBeDisabled = false;
      this.finished.emit(state);
    }),
    map((): TestComponentStatus.WAITING => TestComponentStatus.WAITING)
  );
  private status: TestComponentStatus = TestComponentStatus.WAITING;

  protected constructor(testImplementation: T) {
    super();
    this.state = this.$state.asObservable().pipe(map(this.testStateToUIStateWrapper));
    // this.onChangedRunningState.subscribe(() => {});
    // subscribed to trigger emitting "finished" output if there is no test-series parent
    this.testImplementation = testImplementation;
  }

  public onChangedRunningState: Observable<TestComponentStatus> = merge(
    this.$onStatusNotWaiting.asObservable(),
    this.onStatusWaiting$
  );

  public ngOnInit(): void {
    this.setActive(false);
  }

  public ngOnChanges(): void {
    if (this.status === TestComponentStatus.REQUESTS_CONFIG && this.config !== undefined && this.config !== null) {
      this.start();
    }
  }

  public requestStart = () => {
    if (this.status === TestComponentStatus.WAITING && (this.config === undefined || this.config === null)) {
      this.$onStatusNotWaiting.next(TestComponentStatus.REQUESTS_CONFIG);
      this.status = TestComponentStatus.REQUESTS_CONFIG;
      this.statusChange.emit(TestComponentStatus.REQUESTS_CONFIG);
    } else if (this.status === TestComponentStatus.WAITING && this.config !== undefined && this.config !== null) {
      this.start();
    }
  };

  private start = () => {
    // TODO: consider token
    if (this.status !== TestComponentStatus.WORKING && this.config !== undefined && this.config !== null) {
      this.status = TestComponentStatus.WORKING;
      this.statusChange.emit(TestComponentStatus.WORKING);
      this.manualStartShouldBeDisabled = true;
      this.$onStatusNotWaiting.next(TestComponentStatus.WORKING);
      this.testImplementation.register(this.config, this.$state);
    }
  };

  // WRAPPER NEEDED, since testStateToUIState can not be used in the constructor
  private testStateToUIStateWrapper: (state: TS) => US = (state: TS) => {
    return this.testStateToUIState(state);
  };
}
