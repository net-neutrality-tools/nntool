import { Component, Input } from '@angular/core';
import { ListGrouped } from './measurement.model';

@Component({
  selector: 'app-measurement-grouped-list',
  template: ``
})
export class MeasurementGroupedComponent {
  @Input()
  public values: ListGrouped;
}
