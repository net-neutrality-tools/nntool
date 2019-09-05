import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ResultGroupResponse } from '../../@core/models/result.groups';
import { QoSMeasurementResult, QoSTypeDescription } from '../../@core/models/lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';
import { QoSResultGroupHolder } from '../../@core/models/qos-result-group-holder';
import { UserService } from '../../services/user.service';

@Component({
  templateUrl: './history.view.component.html'
})
export class HistoryViewComponent implements OnInit {
  private measurementUuid: string;
  private response: ResultGroupResponse;
  private loading: boolean;

  private fullMeasurementResponse: any;
  private qosMeasurementResult: QoSMeasurementResult;
  private qosGroups: QoSResultGroupHolder[];

  constructor(
    private activatedRoute: ActivatedRoute,
    private userService: UserService
  ) {
    this.loading = true;
    this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
  }

  public ngOnInit() {
    this.activatedRoute.paramMap.subscribe(paramMap => (this.measurementUuid = paramMap.get('uuid')));
    this.userService.loadMeasurementDetail(this.measurementUuid).subscribe((data: any) => {
      this.loading = false;
      this.response = data.data;
    });

    this.userService.loadFullMeasurement(this.measurementUuid).subscribe((data: any) => {
      this.fullMeasurementResponse = data.data;
      this.qosMeasurementResult = this.fullMeasurementResponse.measurements.QOS;

      const resultMap: Map<string, QoSResultGroupHolder> = new Map();
      this.qosGroups = new Array();
      this.qosMeasurementResult.results.forEach(result => {
        if (!resultMap[result.type]) {
          const groupHolder = new QoSResultGroupHolder();
          const desc: QoSTypeDescription = this.qosMeasurementResult.qos_type_to_description_map[result.type];
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
}
