/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { Component, Input, OnChanges } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { SlideAnimation } from '../../../animation/animation';
import { QoSResultGroupHolder } from '../../models/qos-result-group-holder';
import { SlideableItem } from '../../../animation/slideable-item';
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

  @Input() qosMeasurementResult: QoSMeasurementResult;

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
          groupHolder.successes = 0;
          groupHolder.failures = 0;
          resultMap[result.type] = groupHolder;
          this.qosGroups.push(groupHolder);
        }

        const group = resultMap[result.type];
        if (result.failure_count > 0) {
          group.failures++;
        } else {
          group.successes++;
        }
        
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
