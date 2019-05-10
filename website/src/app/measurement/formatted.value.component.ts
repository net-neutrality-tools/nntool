import {Component, Input} from "@angular/core";
import {FormattedValue} from "./measurement.model";


@Component({
    selector: "measurement-value-formatted",
    template: ``
})
export class MeasurementFormattedValueComponent {
    @Input()
    value: FormattedValue;
}