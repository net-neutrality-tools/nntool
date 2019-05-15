import {Component, Input} from "@angular/core";
import {ClassifiedValue} from "./measurement.model";


@Component({
    selector: "measurement-value-classified",
    template: ``
})
export class MeasurementClassifiedValueComponent {
    @Input()
    value: ClassifiedValue;
}