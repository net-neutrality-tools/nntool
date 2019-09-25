import { Component, Input } from '@angular/core';
import { FormattedValue } from '../../models/measurement.model';

@Component({
  selector: 'app-measurement-value-formatted',
  template: ``
})
export class MeasurementFormattedValueComponent {
  @Input()
  public value: FormattedValue;
}
