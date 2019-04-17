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


export {TestGuard} from "./test.guard";


@Component({
    templateUrl: "./app/test/test.component.html"
})
export class NetTestComponent extends BaseNetTestComponent {

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

    speedTestFinished(speedTestResult: SpeedTestState): void {
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
                    "results": [
                        {
                            "deserialize_type": "speed_result", //qos_result
                            "reason": null,
                            "relative_end_time_ns": null,
                            "relative_start_time_ns": null,
                            "status": null,
                            "results": []
                        }
                    ],
                    "schedule": undefined,
                    "start": undefined,
                    "status": undefined,
                    "tag": undefined,
                    "task": undefined
                }
            ],
            "time_based_result": undefined
        };

        lmapReport.result[0].results[0]['bytes_download'] = speedTestResult.downBit / 8;
        lmapReport.result[0].results[0]['bytes_upload'] = speedTestResult.upBit / 8;
        lmapReport.result[0].results[0]['duration_rtt_ns'] = 1000 * 1000 * 1000;
        lmapReport.result[0].results[0]['duration_download_ns'] = 1000 * 1000 * 1000;
        lmapReport.result[0].results[0]['duration_upload_ns'] = 1000 * 1000 * 1000;
        lmapReport.result[0].results[0]['rtt_info'] = {
            rtts: [
                {
                    rtt_ns: speedTestResult.ping * 1000 * 1000
                }
            ]
        };



        this.testService.postMeasurementResults(lmapReport).subscribe(response => {
        });
    }

    portBlockingTestFinished(portBlockingTestResult: PortBlockingTestState): void {
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
                    "results": [
                        {
                            "deserialize_type": "qos_result", //qos_result
                            "reason": null,
                            "relative_end_time_ns": null,
                            "relative_start_time_ns": null,
                            "status": null,
                            "results": []
                        }
                    ],
                    "schedule": undefined,
                    "start": undefined,
                    "status": undefined,
                    "tag": undefined,
                    "task": undefined
                }
            ],
            "time_based_result": undefined
        };
        let count = 300; //?
        for (const port of portBlockingTestResult.types[0].ports) {
            lmapReport.result[0].results[0]['results'].push(
                {
                    "duration_ns": undefined,
                    "echo_protocol_objective_payload": "UPD payload",
                    "echo_protocol_objective_protocol": "udp",
                    "qos_test_uid": ++count,
                    "echo_protocol_status": port.reachable ? "SUCCESS" : "ERROR",
                    "echo_protocol_objective_port": port.number,
                    "start_time_ns": undefined,
                    "echo_protocol_objective_timeout": undefined,
                    "test_type": "echo_protocol",
                    "echo_protocol_objective_host": "ENTER_QOS_SERVER_ADDRESS"
                }
            );
        }
        this.testService.postMeasurementResults(lmapReport).subscribe(response => {
        });
    }
}