import {Injectable} from "@angular/core";
import {TestImplementation} from "./tests-implementation/test-implementation";
import {TestConfig} from "./tests-implementation/test-config";
import {TestState} from "./tests-implementation/test-state";
import {Subject} from "rxjs";

@Injectable({
    providedIn: 'root',
})
export class TestSchedulerService {

    private current: {test: TestImplementation<TestConfig, TestState>, config: TestConfig, $state: Subject<TestState>} = null;
    private impending: {test: TestImplementation<TestConfig, TestState>, config: TestConfig, $state: Subject<TestState>}[] = [];

    constructor() {
    }

    public register = (test: TestImplementation<TestConfig, TestState>, config: TestConfig, $state: Subject<TestState>) => {
        this.impending.push({test: test, config: config, $state: $state});
        if (this.impending.length === 1 && this.current === null) {
            this.startNextTest();
        }
    }

    private startNextTest = () => {
        if (this.impending.length > 0 && this.current === null) {
            this.current = this.impending.shift();
            this.current.test.start(this.current.config, this.current.$state);
        }
    }

    public reportEnd = (test: TestImplementation<TestConfig, TestState>) => {
        if (this.current.test === test) {
            this.current = null;
            this.startNextTest();
        }
    }
}