import { Component, OnInit, Input } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { SlideAnimation } from '../../../animation/animation';
import { QoSResultGroupHolder } from '../../models/qos-result-group-holder';
import { SlideableItem } from '../../../animation/slideable-item';

@Component({
  selector: 'nntool-qos-result',
  templateUrl: './qos-result.component.html',
  styleUrls: ['./qos-result.component.less'],
  animations: [SlideAnimation]
})
export class QoSResultComponent implements OnInit {

  @Input() qosGroups: QoSResultGroupHolder[];

  constructor(private logger: NGXLogger) { }

  public ngOnInit() {

  }

  // common visibility toggler for both tests and groups
  public toggleSlideAnimation(slideableItem: SlideableItem) {
    SlideableItem.toggleSlideAnimation(slideableItem);
  }
}
