import {AfterViewInit, Component, ContentChildren, ElementRef, Input, QueryList} from "@angular/core";
import {StartableTest} from "./test.component";

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

    // for now
    private fixedTestComponents: StartableTest[];
    private currentTestIndex: number = 0;

    constructor() {
    }

    ngAfterViewInit(): void {
        this.fixedTestComponents = this.testComponents.toArray();
        this.fixedTestComponents.forEach((testComponent: StartableTest) => {
            testComponent.setActive(false);
            testComponent.ended().subscribe(this.testHasEnded);
        });
        this.fixedTestComponents[this.currentTestIndex].setActive(true);
        if (this.autostart) {
            this.fixedTestComponents[this.currentTestIndex].start(true);
        }
    }

    private testHasEnded = (ended: boolean) => {
        if (ended) {
            this.fixedTestComponents[this.currentTestIndex].setActive(false);
            this.currentTestIndex = (this.currentTestIndex + 1) % this.fixedTestComponents.length;
            this.fixedTestComponents[this.currentTestIndex].setActive(true);
            this.fixedTestComponents[this.currentTestIndex].start(true);
        }
    }
}