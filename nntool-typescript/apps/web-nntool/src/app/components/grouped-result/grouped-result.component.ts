import { Component, Input } from '@angular/core';

@Component({
  selector: 'nntool-grouped-result',
  templateUrl: './grouped-result.component.html',
  styleUrls: ['./grouped-result.component.less']
})
export class GroupedResultComponent {
  @Input() response: any;
  @Input() loading: boolean;
}
