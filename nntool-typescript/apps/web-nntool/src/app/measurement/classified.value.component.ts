import { Component, Input } from '@angular/core';
import { ClassifiedValue } from '../@core/models/measurement.model';

@Component({
  selector: 'app-measurement-value-classified',
  template: ``
})
export class MeasurementClassifiedValueComponent {
  @Input()
  public value: ClassifiedValue;
}
