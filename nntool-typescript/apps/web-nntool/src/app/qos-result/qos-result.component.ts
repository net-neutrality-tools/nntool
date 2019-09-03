import { Component, NgZone, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { SlideAnimation } from '../animation/animation';
import { SlideableItem } from '../animation/slideable-item';
import { UserService } from '../services/user.service';
import { FullMeasurementResponse, QoSResult, QoSTypeDescription } from './model/full-measurement-response.api';

@Component({
  templateUrl: './qos.result.component.html',
  selector: 'qos-result-component',
  animations: [SlideAnimation]
})
export class QoSResultComponent implements OnInit {
  private measurementUuid: string;
  private response: FullMeasurementResponse;
  private qosGroups: QoSResultGroupHolder[];

  constructor(
    private logger: NGXLogger,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private ngZone: NgZone
  ) {
    this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
  }

  // common visibility toggler for both tests and groups
  public toggleSlideAnimation(slideableItem: SlideableItem) {
    SlideableItem.toggleSlideAnimation(slideableItem);
  }

  public ngOnInit() {
    this.activatedRoute.paramMap.subscribe(paramMap => (this.measurementUuid = paramMap.get('uuid')));
    this.userService.loadFullMeasurement(this.measurementUuid).subscribe((data: any) => {
      this.response = data.data;
      this.logger.debug('result: ', this.response);
      this.logger.debug('measurements: ', this.response.measurements.QOS);
      const resultMap: Map<string, QoSResultGroupHolder> = new Map();
      this.qosGroups = new Array();
      this.response.measurements.QOS.results.forEach(result => {
        if (!resultMap[result.type]) {
          const groupHolder = new QoSResultGroupHolder();
          const desc: QoSTypeDescription = this.response.measurements.QOS.qos_type_to_description_map[result.type];
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
    });
  }

  protected resetData(cb?: () => void): void {}
}

export class QoSResultGroupHolder extends SlideableItem {
  public icon: string;
  public title: string;
  public description: string;
  public successes = 0;
  public failures = 0;
  public tests = new Array<QoSResult>();
}
