import { Component, Input } from '@angular/core';
import { ClassifiedValue } from './measurement.model';

@Component({
  selector: 'app-measurement-value-classified',
  template: ``
})
export class MeasurementClassifiedValueComponent {
  @Input()
  public value: ClassifiedValue;
}
