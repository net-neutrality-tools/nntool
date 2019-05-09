import {Component, ElementRef, NgZone} from "@angular/core";
import {ActivatedRoute} from "@angular/router";

import {TranslateService} from "@ngx-translate/core";
import {BaseNetTestComponent} from "./base_test.component";
import {LoggerService} from "../services/log.service";
import {ConfigService} from "../services/config.service";
import {UserService} from "../services/user.service";
import {RequestsService} from "../services/requests.service";
import {AppService} from "../services/app.service";
import {TestService} from "../services/test/test.service";
import {SpeedTestState} from "../testing/tests-implementation/speed/speed-test-state";
import {PortBlockingTestState} from "../testing/tests-implementation/port-blocking/port-blocking-test-state";
import {LmapReportAPI} from "./models/measurements/lmap-report.api";
import {LmapOptionAPI, MeasurementTypeParameters} from "./models/measurements/lmap-option.api";
import {LmapTaskAPI} from "./models/measurements/lmap-task.api";
import {LmapControlAPI} from "./models/measurements/lmap-control.api";
import {TestComponentStatus} from "../testing/enums/test-component-status.enum";
import {
    QoSMeasurementResult,
    RttInfo,
    SpeedMeasurementResult,
    SubMeasurementResult
} from "./models/measurements/lmap-result.api";


export {TestGuard} from "./test.guard";


@Component({
    templateUrl: "./app/test/test.component.html"
})
export class NetTestComponent extends BaseNetTestComponent {

    protected measurementControl: LmapControlAPI = undefined;
    private speedControl: LmapTaskAPI = undefined;
    public speedConfig: MeasurementTypeParameters = undefined; // TODO: change to measurement configuration
    private qosControl: LmapTaskAPI = undefined;
    public qosConfig: MeasurementTypeParameters = undefined; // TODO: change to measurement configuration
    private testResults: (SpeedMeasurementResult | QoSMeasurementResult)[] = [];

    constructor (
        testService: TestService,
        configService: ConfigService, userService: UserService,
        translateService: TranslateService,
        requests: RequestsService, elementRef: ElementRef,
        zone: NgZone, activatedRoute: ActivatedRoute, appService: AppService, private requestService: RequestsService
    ) {
        super(testService, configService, userService, translateService, requests, elementRef, zone, activatedRoute, appService);
        this.logger = LoggerService.getLogger("NetTestComponent");
    }

    ngOnInit () {
        super.ngOnInit();
        this.screenNr = 1;
    }

    agree (): void {
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

    private requestMeasurement (): void {
        this.testService.newMeasurement().subscribe(response => {
            this.processTestControl(response);
            this.measurementControl = response;
        });
    }

    private processTestControl (measurementControl: LmapControlAPI): void {
        if (measurementControl && measurementControl.tasks) {
            measurementControl.tasks.forEach((task: LmapTaskAPI) => {
                switch (task.name) {
                    case 'SPEED':
                        this.speedConfig = task.option.reduce(
                            (config: any, option: LmapOptionAPI) => {
                                return option.name === 'parameters_speed' ?
                                    option["measurement-parameters"]["measurement_configuration"]
                                    : config;
                            }, {});
                        this.speedControl = task;
                        break;
                    case 'QOS':
                        this.qosConfig = task.option.reduce(
                            (config: any, option: LmapOptionAPI) => {
                                return option.name === "parameters_qos" ?
                                    option["measurement-parameters"]["objectives"]
                                    : config;
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

        const lmapReport: LmapReportAPI = {
            "additional_request_info": undefined,
            "agent-id": undefined,
            "date": undefined,
            "group-id": undefined,
            "measurement-point": undefined,
            "result": [
                {
                    "action": undefined,
                    "conflict": undefined,
                    "cycle-number": undefined,
                    "end": undefined,
                    "event": undefined,
                    "option": undefined,
                    "parameters": undefined,
                    "results": [],
                    "schedule": undefined,
                    "start": undefined,
                    "status": undefined,
                    "tag": undefined,
                    "task": undefined
                }
            ],
            "time_based_result": undefined
        };

        this.testResults.forEach((subMeasurementResult: (SpeedMeasurementResult | QoSMeasurementResult)) => {
            lmapReport.result[0].results.push(subMeasurementResult);
        });

        this.testService.postMeasurementResults(
            lmapReport,
            this.speedControl.option.reduce(
                (url: string, option: LmapOptionAPI) => option.name === 'result_collector_base_url' ? (url + option.value) : url, '')
        ).subscribe(response => {
        });

        this.testResults = [];
    }

    speedTestFinished(speedTestResult: SpeedTestState): void {
        const speedMeasurementResult: SpeedMeasurementResult = new SpeedMeasurementResult();

        speedMeasurementResult.deserialize_type = "speed_result";
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
        qosMeasurementResult.deserialize_type = "qos_result";
        qosMeasurementResult.reason = null;
        qosMeasurementResult.relative_end_time_ns = null;
        qosMeasurementResult.relative_start_time_ns = null;
        qosMeasurementResult.status = null;
        qosMeasurementResult.results = [];

        for (const port of portBlockingTestResult.types[0].ports) {
            qosMeasurementResult.results.push(
                {
                    "udp_result_out_num_packets": 1,
                    "udp_objective_out_num_packets": 1,
                    "qos_test_uid": port.uid,
                    "test_type": "udp",
                    "udp_result_out_response_num_packets": port.reachable ? 1 : 0,
                    "udp_result_out_packet_loss_rate": port.reachable ? 0 : 100,
                    "udp_objective_out_port": port.number
                }
            );
        }

        this.testResults.push(qosMeasurementResult);
    }
}