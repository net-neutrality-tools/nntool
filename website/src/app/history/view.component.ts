import {MeasurementViewComponent} from "../measurement/view.component";
import {Component} from "@angular/core";


@Component({
    templateUrl: "./app/measurement/view.component.html"
})
export class HistoryViewComponent extends MeasurementViewComponent {
    protected resetData (cb?: () => void): void {}

}