import { Component, Input } from '@angular/core';

@Component({
  selector: 'nntool-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.less']
})
export class ResultComponent {
  @Input() response: any;
}
