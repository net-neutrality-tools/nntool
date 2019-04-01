import {Component, Input} from "@angular/core";

import {ListGrouped} from "./measurement.model";


@Component({
    selector: "measurement-grouped-list",
    template: ``
})
export class MeasurementGroupedComponent {
    @Input()
    values: ListGrouped;
}