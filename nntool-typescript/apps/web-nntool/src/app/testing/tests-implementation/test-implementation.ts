import {TestState} from './test-state';
import {Subject, Subscription} from 'rxjs';
import {TestConfig} from './test-config';
import {TestSchedulerService} from '../test-scheduler.service';
import {BasicTestState} from '../enums/basic-test-state.enum';

export abstract class TestImplementation<TC extends TestConfig, TS extends TestState> {

    private stateSubscription: Subscription;

    protected constructor(private testSchedulerService: TestSchedulerService) {}

    protected abstract generateInitState: (config: TC) => TS;
    public abstract start: (config: TC, $state: Subject<TS>) => void;
    protected abstract clean: () => void;

    public register = (config: TC, $state: Subject<TS>): void => {
        this.stateSubscription = $state.asObservable().subscribe(this.end);
        $state.next(this.generateInitState(config));
        this.testSchedulerService.register(this, config, $state);
    }

    public end = (state: TS): void => {
        if (state.basicState === BasicTestState.ENDED && this.stateSubscription) {
            this.stateSubscription.unsubscribe();
            this.clean();
            this.testSchedulerService.reportEnd(this);
        }
    }
}
