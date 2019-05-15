import {AfterViewInit, Component, ContentChildren, EventEmitter, Input, Output, QueryList} from "@angular/core";
import {StartableTest} from "./test.component";
import {TestComponentStatus} from "./enums/test-component-status.enum";

@Component({
    template: '<ng-content></ng-content>',
    selector: "test-series"
})
export class TestSeriesComponent implements AfterViewInit {

    @ContentChildren('startableTest') testComponents: QueryList<StartableTest>;

    @Input()
    private autostart: boolean = false;

    @Input()
    private visible: boolean = true;

    @Output()
    private statusChange: EventEmitter<TestComponentStatus> = new EventEmitter<TestComponentStatus>();

    // for now
    private currentFixedTestComponents: StartableTest[];
    private currentTestIndex: number = 0;

    constructor() {

    }

    ngAfterViewInit(): void {
        /*this.testComponents.changes.subscribe(
            (testComponents) => {
                const test = this.currentFixedTestComponents;
                console.log(test);
            }
        );*/
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
    }
}