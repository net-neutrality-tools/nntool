 import {Component} from "@angular/core";


import {MeasurementDetailViewComponent} from "../measurement/view-detail.component";


interface ITestType {
    key: string;
    desc: string;
    name: string;
    tests: any[];
    successes: number;
    failures: number;
    icon: string;
    shown: boolean;
}


@Component({
    templateUrl: "../measurement/view-detail.component.html"
})
export class HistoryDetailViewComponent extends MeasurementDetailViewComponent {

    protected resetData (cb?: () => void): void {}
}