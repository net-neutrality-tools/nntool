import {MeasurementViewComponent} from '../measurement/view.component';
import {Component, OnInit} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../services/config.service';
import { RequestsService } from '../services/requests.service';
import { LoggerService, Logger } from '../services/log.service';
import { WebsiteSettings } from '../settings/settings.interface';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';
import { FullMeasurementResponse, QoSResult, QoSTypeDescription } from './model/full-measurement-response.api';


@Component({
    templateUrl: './qos.result.component.html',
    selector: 'qos-result-component'
})
export class QoSResultComponent extends MeasurementViewComponent implements OnInit {

    private configService: ConfigService;
    private requests: RequestsService;
    private logger: Logger = LoggerService.getLogger('HistoryViewComponent');
    private config: WebsiteSettings;
    private translationKey: string;
    private urlpath: string;
    private measurementUuid: string;
    private loading: boolean;
    private response: FullMeasurementResponse;
    private qosGroups: QoSResultGroupHolder[];
    private testTypeShown: boolean = false;

    constructor(private translationService: TranslateService, private activatedRoute: ActivatedRoute,
                private userService: UserService) {
        super();
        this.loading = true;
        this.urlpath = '/history/';
        this.translationKey = 'RESULT.DETAIL';
        this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
    }

    protected resetData(cb?: () => void): void {}


    ngOnInit() {
        this.activatedRoute.paramMap.subscribe( paramMap => this.measurementUuid = paramMap.get('uuid'));
        this.userService.loadFullMeasurement(this.measurementUuid).subscribe(
            (data: any) => {
                this.loading = false;
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
                    group.tests.push(result);
                });
            }
        );
    }

}

export class QoSResultGroupHolder {
    icon: string;
    title: string;
    description: string;
    successes: number = 0;
    failures: number = 0;
    tests: QoSResult[] = new Array();

}
