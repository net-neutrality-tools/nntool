import {TestImplementation} from "./tests-implementation/test-implementation";
import {UIState} from "./tests-ui/ui-state";
import {TestState} from "./tests-implementation/test-state";
import {Observable, Subject} from "rxjs";
import {map, filter, tap} from "rxjs/operators";
import {Component, EventEmitter, Input, Output} from "@angular/core";
import {BasicTestStateEnum} from "./enums/basic-test-state.enum";

@Component({
})
export abstract class StartableTest {
    start: (force: boolean) => void;
    ended: () => Observable<boolean>;
    setActive: (active: boolean) => void;
}

@Component({
})
export abstract class Test<
    US extends UIState,
    T extends TestImplementation<TS>,
    TS extends TestState
    > extends StartableTest {

    @Input()
    protected autostart: boolean = false;

    @Output()
    private finished: EventEmitter<TS> = new EventEmitter<TS>();

    @Output()
    protected errorMsgChange: EventEmitter<string> = new EventEmitter<string>();

    @Output()
    protected measurementLinkChange: EventEmitter<string> = new EventEmitter<string>();

    @Output()
    protected testInProgressChange: EventEmitter<boolean> = new EventEmitter<boolean>();


    private testImplementation: T;
    private $state: Subject<TS> = new Subject<TS>();
    protected state: Observable<US>;
    private hasEnded: boolean;

    protected constructor(testImplementation: T) {
        super();
        this.state = this.$state.asObservable().pipe(
            map(this.testStateToUIStateWrapper)
        );
        this.testImplementation = testImplementation;
        this.testImplementation.init(this.$state);
        this.start(false); // TODO: REMOVE
    }

    public start = (force: boolean) => {
        if (force || this.autostart) {
            this.hasEnded = false;
            this.testImplementation.start();
        }
    }

    private destroy = () => {
    }

    public ended = (): Observable<boolean> => {
        return this.$state.asObservable().pipe(
            filter((state: TS) => {
                return state.basicState === BasicTestStateEnum.ENDED && !this.hasEnded; // TODO: find better solution than hasEnded
            }),
            tap((state: TS) => {
                this.hasEnded = true;
                this.finished.emit(state);
            }),
            map(() => true)
        );
    }

    protected abstract testStateToUIState: (state: TS) => US;

    // WRAPPER NEEDED, SINCE testStateToUIState can not be used in the constructor
    private testStateToUIStateWrapper: (state: TS) => US = (state: TS) => {
        return this.testStateToUIState(state);
    }
}