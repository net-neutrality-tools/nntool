import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { SlideAnimation } from '../../animation/animation';
import { SlideableItem } from '../../animation/slideable-item';
import { UserService } from '../../services/user.service';
import { FullMeasurementResponse, QoSResult, QoSTypeDescription } from '../../qos-result/model/full-measurement-response.api';
import { QoSResultGroupHolder } from '../../qos-result/model/qos-result-group-holder';
import { Observable } from '../../../../../../node_modules/rxjs';
import { QoSMeasurementResult } from '../../lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';

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
