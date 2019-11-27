import { Subject, Subscription } from 'rxjs';
import { BasicTestState } from '../enums/basic-test-state.enum';
import { TestSchedulerService } from '../test-scheduler.service';
import { TestConfig } from './test-config';
import { TestState } from './test-state';

export abstract class TestImplementation<TC extends TestConfig, TS extends TestState> {
  public abstract start: (config: TC, $state: Subject<TS>) => void;

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
