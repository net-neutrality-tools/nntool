import {TestImplementation} from "./tests-implementation/test-implementation";
import {UIState} from "./tests-ui/ui-state";
import {TestState} from "./tests-implementation/test-state";
import {Observable, Subject} from "rxjs";
import {map, tap} from "rxjs/operators";
import {Component, EventEmitter, Input, Output} from "@angular/core";
import {BasicTestStateEnum} from "./enums/basic-test-state.enum";

@Component({
})
export abstract class StartableTest {
    start: (force: boolean) => void;
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
    protected errorMsgChange: EventEmitter<string> = new EventEmitter<string>();

    @Output()
    protected measurementLinkChange: EventEmitter<string> = new EventEmitter<string>();

    @Output()
    protected testInProgressChange: EventEmitter<boolean> = new EventEmitter<boolean>();

    @Input()
    protected set visible (value: boolean) {
        // TODO implement missing functionality of old interface
        /*this.isVisible = value;
        if (this.isVisible && this.testGauge) {
            setTimeout(() => {
                this.testGauge.resizeEvent();
            }, 0);
        }*/
    }

    private testImplementation: T;
    private $state: Subject<TS> = new Subject<TS>();
    protected state: Observable<US>;

    // TODO: REPLACE TEMPCALLBACK WITH OBSERVABLES AND FILTERS
    private tempCallback?: () => void;

    protected constructor(testImplementation: T) {
        super();
        this.state = this.$state.asObservable().pipe(
            tap(this.hasEnded),
            map(this.testStateToUIStateWrapper)
        );
        this.testImplementation = testImplementation;
        this.testImplementation.init(this.$state);
    }

    // TODO: REPLACE TEMPCALLBACK WITH OBSERVABLES AND FILTERS
    public start = (force: boolean) => {
        if (force || this.autostart) {
            this.testImplementation.start();
        }
    }

    public addTempCallback = (tempCallback?: () => void) => {
        this.tempCallback = tempCallback;
    }


    private destroy = () => {
    }

    private hasEnded = (state: TS): void => {
        if (state.basicState === BasicTestStateEnum.ENDED && this.tempCallback) {
            this.tempCallback();
        }
    }

    protected abstract testStateToUIState: (state: TS) => US;

    // WRAPPER NEEDED, SINCE testStateToUIState can not be used in the constructor
    private testStateToUIStateWrapper: (state: TS) => US = (state: TS) => {
        return this.testStateToUIState(state);
    }
}