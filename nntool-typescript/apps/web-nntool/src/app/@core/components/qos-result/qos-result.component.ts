import { Component, Input, OnChanges } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { SlideAnimation } from '../../../animation/animation';
import { QoSResultGroupHolder } from '../../models/qos-result-group-holder';
import { SlideableItem } from '../../../animation/slideable-item';
import { Observable } from 'rxjs';
import { QoSMeasurementResult } from '../../models/lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';
import { QoSTypeDescription } from '../../models/full-measurement-response.api';

@Component({
  selector: 'nntool-qos-result',
  templateUrl: './qos-result.component.html',
  styleUrls: ['./qos-result.component.less'],
  animations: [SlideAnimation]
})
export class QoSResultComponent implements OnChanges {

  qosGroups: QoSResultGroupHolder[];

  @Input() qosMeasurementResult: Observable<QoSMeasurementResult>;

  constructor(private logger: NGXLogger) { }

  ngOnChanges(changes) {
    if (changes.qosMeasurementResult && changes.qosMeasurementResult.currentValue) {
      const resultMap: Map<string, QoSResultGroupHolder> = new Map();
      this.qosGroups = new Array();
      changes.qosMeasurementResult.currentValue.results.forEach(result => {
        if (!resultMap[result.type]) {
          const groupHolder = new QoSResultGroupHolder();
          const desc: QoSTypeDescription = changes.qosMeasurementResult.currentValue.qos_type_to_description_map[result.type];
          groupHolder.icon = desc.icon;
          groupHolder.title = desc.name;
          groupHolder.description = desc.description;
          resultMap[result.type] = groupHolder;
          this.qosGroups.push(groupHolder);
        }

        const group = resultMap[result.type];
        group.successes += result.success_count;
        group.failures += result.failure_count;

        // need to manually add hidden here, as the preset value doesn't apply correctly w/the deserialization
        result.showSlideableItem = false;
        group.tests.push(result);
      });
    }
  }

  // common visibility toggler for both tests and groups
  public toggleSlideAnimation(slideableItem: SlideableItem) {
    SlideableItem.toggleSlideAnimation(slideableItem);
  }
}
