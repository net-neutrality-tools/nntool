import {
    AfterViewInit,
    Component,
    ContentChildren,
    EventEmitter,
    Input,
    OnInit,
    Output,
    QueryList,
    Type
} from "@angular/core";
import {SpeedTestGauge} from "./tests/speed-test-gauge";
import {PortBlockingTestBar} from "./tests/port-blocking-test-bar";

@Component({
    template: '<ng-content></ng-content>',
    selector: "test-series"
})
export class TestSeriesComponent implements OnInit, AfterViewInit {

    // TODO: Replace by superclass
    @ContentChildren(SpeedTestGauge) speedTestGaugeTestComponents: QueryList<SpeedTestGauge>;
    @ContentChildren(PortBlockingTestBar) portBlockingTestBarTestComponents: QueryList<PortBlockingTestBar>;

    @Input()
    private autostart: boolean = false;

    @Output()
    private currentTestIndex = new EventEmitter<number>();

    constructor() {
    }

    ngOnInit(): void {
        setTimeout(() => {
            this.currentTestIndex.emit(1);
        }, 0); // TODO: REPLACE SET TIMEOUT
    }
    ngAfterViewInit(): void {
        // TODO: REPLACE BY GENERIC DESIGN
        // TODO: REMOVE
        this.speedTestGaugeTestComponents.changes.subscribe((speedTestGaugeTestComponents) => {
            if (speedTestGaugeTestComponents.length > 0) {
                speedTestGaugeTestComponents.first.addTempCallback(() => {
                    this.currentTestIndex.emit(2);
                });
                if (this.autostart) {
                    speedTestGaugeTestComponents.first.start(true);
                }
            }
        });
        this.portBlockingTestBarTestComponents.changes.subscribe((portBlockingTestBarTestComponents) => {
            if (portBlockingTestBarTestComponents.length > 0) {
                portBlockingTestBarTestComponents.first.addTempCallback(() => {
                    this.currentTestIndex.emit(1);
                });
                portBlockingTestBarTestComponents.first.start(true);
            }
        });
    }
}