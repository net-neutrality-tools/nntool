import {Component, ElementRef, NgZone, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {TranslateService} from '@ngx-translate/core';
import {BaseNetTestComponent} from './base_test.component';
import {LoggerService} from '../services/log.service';
import {ConfigService} from '../services/config.service';
import {UserService} from '../services/user.service';
import {RequestsService} from '../services/requests.service';
import {AppService} from '../services/app.service';
import {TestService} from '../services/test/test.service';
import {SpeedTestState} from '../testing/tests-implementation/speed/speed-test-state';
import {PortBlockingTestState} from '../testing/tests-implementation/port-blocking/port-blocking-test-state';
import {TestComponentStatus} from '../testing/enums/test-component-status.enum';
import {LmapControl} from '../lmap/models/lmap-control.model';
import {LmapTask} from '../lmap/models/lmap-control/lmap-task.model';
import {LmapOption, MeasurementTypeParameters} from '../lmap/models/shared/lmap-option.model';
import {SpeedMeasurementResult} from '../lmap/models/lmap-report/lmap-result/extensions/speed-measurement-result.model';
import {QoSMeasurementResult} from '../lmap/models/lmap-report/lmap-result/extensions/qos-measurement-result.model';
import {RttInfo} from '../lmap/models/lmap-report/lmap-result/extensions/rtt-info.model';
import {LmapReport} from '../lmap/models/lmap-report.model';
import {LocationService} from '../services/location.service';
import {TimeBasedResultAPI} from './models/measurements/time-based-result.api';
import {LmapResult} from '../lmap/models/lmap-report/lmap-result.model';
import {DeviceDetectorService} from 'ngx-device-detector';



export {TestGuard} from './test.guard';


@Component({
    templateUrl: './test.component.html'
})
export class NetTestComponent extends BaseNetTestComponent implements OnInit {

    protected measurementControl: LmapControl = undefined;
    private speedControl: LmapTask = undefined;
    public speedConfig: MeasurementTypeParameters = undefined; // TODO: change to measurement configuration
    private qosControl: LmapTask = undefined;
    public qosConfig: MeasurementTypeParameters = undefined; // TODO: change to measurement configuration
    private testResults: (SpeedMeasurementResult | QoSMeasurementResult)[] = [];
    private startTimeStamp: string = undefined;

    constructor(
        testService: TestService,
        configService: ConfigService, userService: UserService,
        translateService: TranslateService,
        requests: RequestsService, elementRef: ElementRef,
        zone: NgZone, activatedRoute: ActivatedRoute, appService: AppService,
        private locationService: LocationService,
        private deviceService: DeviceDetectorService

    ) {
        super(testService, configService, userService, translateService, requests, elementRef, zone, activatedRoute, appService);
        this.logger = LoggerService.getLogger('NetTestComponent');
    }

    ngOnInit() {
        super.ngOnInit();
        this.screenNr = 1;
    }

    agree(): void {
        super.agree();
    }

    handleTestSeriesStatusChange(testComponentStatus: TestComponentStatus) {
        this.testInProgress = testComponentStatus !== TestComponentStatus.WAITING;
        if (testComponentStatus === TestComponentStatus.REQUESTS_CONFIG && this.user.acceptTC) {
            this.requestMeasurement();
        }
        if (testComponentStatus === TestComponentStatus.WAITING && this.user.acceptTC) {
            this.testSeriesFinished();
        }
    }

    private requestMeasurement(): void {
        this.testService.newMeasurement().subscribe(response => {
            this.processTestControl(response);
            this.measurementControl = response;
            this.locationService.startTracking();
            this.startTimeStamp = (new Date()).toJSON().slice(0, -1);
        });
    }

    private processTestControl(measurementControl: LmapControl): void {
        if (measurementControl && measurementControl.tasks) {
            measurementControl.tasks.forEach((task: LmapTask) => {
                switch (task.name) {
                    case 'SPEED':
                        this.speedConfig = task.option.reduce(
                            (config: any, option: LmapOption) => {
                                /* tslint:disable:no-string-literal */
                                return option.name === 'parameters_speed' ?
                                    option['measurement-parameters']['measurement_configuration']
                                    : config;
                                /* tslint:enable:no-string-literal */
                            }, {});
                        this.speedControl = task;
                        break;
                    case 'QOS':
                        this.qosConfig = task.option.reduce(
                            (config: any, option: LmapOption) => {
                                /* tslint:disable:no-string-literal */
                                return option.name === 'parameters_qos' ?
                                    option['measurement-parameters']['objectives']
                                    : config;
                                /* tslint:enable:no-string-literal */
                            }, {});
                        this.qosControl = task;
                        break;
                }
            });
        }
    }

    private testSeriesFinished(): void {
        if (this.testResults.length === 0) {
            return;
        }

        const endTimeStamp = (new Date()).toJSON().slice(0, -1);
        let scheduleName: string;
        if (this.measurementControl.schedules && this.measurementControl.schedules.length > 0) {
            scheduleName = this.measurementControl.schedules[0].name;
        }
        const lmapReport: LmapReport = new LmapReport();
        lmapReport.date = endTimeStamp;
        lmapReport.result = [new LmapResult()];
        lmapReport.result[0].schedule = scheduleName;
        lmapReport.result[0].results = [];
        lmapReport.result[0].event = this.startTimeStamp;
        lmapReport.result[0].start = this.startTimeStamp;
        lmapReport.result[0].end = endTimeStamp;

        this.testResults.forEach((subMeasurementResult: (SpeedMeasurementResult | QoSMeasurementResult)) => {
            lmapReport.result[0].results.push(subMeasurementResult);
        });

        lmapReport.time_based_result = new TimeBasedResultAPI();

        this.locationService.stopTracking();
        lmapReport.time_based_result.geo_locations = this.locationService.getLocations();

        this.testService.postMeasurementResults(
            lmapReport,
            this.speedControl.option.reduce(
                (url: string, option: LmapOption) => option.name === 'result_collector_base_url' ? (url + option.value) : url, '')
        ).subscribe(response => {
        });

        this.testResults = [];
    }

    speedTestFinished(speedTestResult: SpeedTestState): void {
        const speedMeasurementResult: SpeedMeasurementResult = new SpeedMeasurementResult();

        speedMeasurementResult.deserialize_type = 'speed_result';
        speedMeasurementResult.reason = null;
        speedMeasurementResult.relative_end_time_ns = null;
        speedMeasurementResult.relative_start_time_ns = null;
        speedMeasurementResult.status = null;
        speedMeasurementResult.bytes_download = speedTestResult.downBit / 8;
        speedMeasurementResult.bytes_upload = speedTestResult.upBit / 8;
        speedMeasurementResult.duration_rtt_ns = 1000 * 1000 * 1000;
        speedMeasurementResult.duration_download_ns = 1000 * 1000 * 1000;
        speedMeasurementResult.duration_upload_ns = 1000 * 1000 * 1000;
        speedMeasurementResult.rtt_info = new RttInfo();
        speedMeasurementResult.rtt_info.rtts = [
            {
                rtt_ns: speedTestResult.ping * 1000 * 1000,
                relative_time_ns: undefined
            }
        ];

        this.testResults.push(speedMeasurementResult);
    }

    portBlockingTestFinished(portBlockingTestResult: PortBlockingTestState): void {
        const qosMeasurementResult: QoSMeasurementResult = new QoSMeasurementResult();
        qosMeasurementResult.deserialize_type = 'qos_result';
        qosMeasurementResult.reason = null;
        qosMeasurementResult.relative_end_time_ns = null;
        qosMeasurementResult.relative_start_time_ns = null;
        qosMeasurementResult.status = null;
        qosMeasurementResult.results = [];

        for (const port of portBlockingTestResult.types[0].ports) {
            qosMeasurementResult.results.push(
                {
                    udp_result_out_num_packets: 1,
                    udp_objective_out_num_packets: 1,
                    qos_test_uid: port.uid,
                    test_type: 'udp',
                    udp_result_out_response_num_packets: port.reachable ? 1 : 0,
                    udp_result_out_packet_loss_rate: port.reachable ? 0 : 100,
                    udp_objective_out_port: port.number
                }
            );
        }

        this.testResults.push(qosMeasurementResult);
    }
}
