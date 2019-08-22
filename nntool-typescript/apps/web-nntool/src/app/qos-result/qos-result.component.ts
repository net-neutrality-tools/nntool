import {MeasurementViewComponent} from '../measurement/view.component';
import {Component, OnInit, NgZone} from '@angular/core';
import { LoggerService, Logger } from '../services/log.service';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';
import { FullMeasurementResponse, QoSResult, QoSTypeDescription } from './model/full-measurement-response.api';
import { SlideAnimation } from '../animation/animation';
import { SlideableItem } from '../animation/slideable-item';


@Component({
    templateUrl: './qos.result.component.html',
    selector: 'qos-result-component',
    animations: [SlideAnimation]
})
export class QoSResultComponent extends MeasurementViewComponent implements OnInit {

    private logger: Logger = LoggerService.getLogger('HistoryViewComponent');
    private measurementUuid: string;
    private response: FullMeasurementResponse;
    private qosGroups: QoSResultGroupHolder[];

    constructor(private activatedRoute: ActivatedRoute,
                private userService: UserService, private ngZone: NgZone) {
        super();
        this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
    }

    protected resetData(cb?: () => void): void {}

    //common visibility toggler for both tests and groups
    toggleSlideAnimation(slideableItem: SlideableItem) {
        SlideableItem.toggleSlideAnimation(slideableItem);
    }

    ngOnInit() {
        this.activatedRoute.paramMap.subscribe( paramMap => this.measurementUuid = paramMap.get('uuid'));
        this.userService.loadFullMeasurement(this.measurementUuid).subscribe(
            (data: any) => {
                this.response = data.data;
                this.logger.debug("result: ", this.response);
                this.logger.debug("measurements: ", this.response.measurements.QOS);
                let resultMap: Map<string, QoSResultGroupHolder> = new Map();
                this.qosGroups = new Array();
                this.response.measurements.QOS.results.forEach( result => {
                    if (!resultMap[result.type]) {
                        let group: QoSResultGroupHolder = new QoSResultGroupHolder();
                        const desc: QoSTypeDescription = this.response.measurements.QOS.qos_type_to_description_map[result.type];
                        group.icon = desc.icon;
                        group.title = desc.name;
                        group.description = desc.description;
                        resultMap[result.type] = group;
                        this.qosGroups.push(group);
                    }
                    let group: QoSResultGroupHolder = resultMap[result.type];
                    group.successes += result.success_count;
                    group.failures += result.failure_count;
                    //need to manually add hidden here, as the preset value doesn't apply correctly w/the deserialization
                    result.showSlideableItem = false;
                    group.tests.push(result);
                });
            }
        );
    }

}

export class QoSResultGroupHolder extends SlideableItem {
    icon: string;
    title: string;
    description: string;
    successes: number = 0;
    failures: number = 0;
    tests: QoSResult[] = new Array();
}
