import {TestImplementation} from "../test-implementation";
import {SpeedTestState} from "./speed-test-state";
import {Logger, LoggerService} from "../../../services/log.service";
import {Injectable, NgZone} from "@angular/core";
import {Subject} from "rxjs";
import {BasicTestStateEnum} from "../../enums/basic-test-state.enum";
import {SpeedTestStateEnum} from "./enums/speed-test-state.enum";

@Injectable({
    providedIn: 'root',
})
export class SpeedTestImplementation extends TestImplementation<SpeedTestState> {

    protected logger: Logger = LoggerService.getLogger("SpeedTestImplementation");
    private $state: Subject<SpeedTestState>;
    private config: any;
    private speed: any;

    constructor(private zone: NgZone) { // TODO: Add missing services
        super();
    }

    public init = ($state: Subject<SpeedTestState>): void => {
        this.$state = $state;

        const rttRequests = 10;
        const rttRequestTimeout = 2000;
        const rttRequestWait = 500;
        const rttDuration = (rttRequests * (rttRequestTimeout + rttRequestWait)) * 1.1;
        const downloadUploadDuration = 10000;

        this.config = {
            "cmd":"start",
            "platform":"web",
            "wsTargets":["peer-ias-de-01"],
            "wsTargetsRtt":["peer-ias-de-01"],
            "wsTLD":"net-neutrality.tools",
            "wsTargetPort":"80",
            "wsWss":0,
            "wsAuthToken":"placeholderToken",
            "wsAuthTimestamp":"placeholderTimestamp",
            "wsWorkerPath": "./lib/@zafaco/breitbandtest/Worker.js",
            "performRouteToClientLookup":false,
            "performRttMeasurement":true,
            "performDownloadMeasurement":true,
            "performUploadMeasurement":true,
            "wsParallelStreamsDownload":4,
            "wsFrameSizeDownload":32768,
            "downloadThroughputLowerBoundMbps":0.95,
            "downloadThroughputUpperBoundMbps":525,
            "wsParallelStreamsUpload":4,
            "wsFrameSizeUpload":65535,
            "uploadThroughputLowerBoundMbps":0.95,
            "uploadThroughputUpperBoundMbps":525,
            "uploadFramesPerCall": 1,
            "wsRttRequests": rttRequests,
            "wsRttRequestTimeout": rttRequestTimeout,
            "wsRttRequestWait": rttRequestWait,
            "wsRttTimeout": rttDuration,
            "wsMeasureTime": downloadUploadDuration,
            "rtt": {"performMeasurement": true},
            "download": {"performMeasurement": true},
            "upload": {"performMeasurement": true}
        };

        this.speed = new Ias();

        this.zone.runOutsideAngular(() => {
            const state: SpeedTestState = new SpeedTestState();
            state.basicState =  BasicTestStateEnum.INITIALIZED;
            state.speedTestState =  SpeedTestStateEnum.READY;
            state.uuid = null;
            state.serverName = null;
            state.remoteIp = null;
            state.provider = null;
            state.location = null;
            state.device = null;
            state.technology = "BROWSER";

            window.iasCallback = (data: any) => { // TODO: inject window object properly
                const currentState = JSON.parse(data);
                state.device = currentState.device_info.browser_info.name.split(" ")[0];
                if (currentState.test_case !== "port_blocking") {
                    switch (currentState.test_case) {
                        case "rtt":
                            state.basicState = BasicTestStateEnum.RUNNING;
                            if (currentState.cmd === "report") {
                                state.speedTestState = SpeedTestStateEnum.PING;
                                state.ping = currentState.rtt_info.average_ns / (1000 * 1000);
                                state.progress = (
                                    currentState.rtt_info.num_received
                                    + currentState.rtt_info.num_missing
                                    + currentState.rtt_info.num_error
                                ) / rttRequests;
                            }
                            if (currentState.cmd === "finish") {
                                state.speedTestState = SpeedTestStateEnum.PING_OK;
                                state.ping = currentState.rtt_info.average_ns / (1000 * 1000);
                                state.progress = 1;
                            }
                            break;
                        case "download":
                            var currentDownload;
                            if (currentState.cmd === "report") {
                                state.speedTestState = SpeedTestStateEnum.DOWN;
                                currentDownload = currentState.download_info[currentState.download_info.length - 1];
                                state.downBit = currentDownload.throughput_avg_bps;
                                state.downMBit = state.downBit / (1000 * 1000);
                                state.downMBit = state.downMBit.toFixed(2);
                                state.progress = (currentDownload.duration_ns
                                 / (1000.0 * 1000.0)) / downloadUploadDuration;
                            }
                            if (currentState.cmd === "finish") {
                                currentDownload = currentState.download_info[currentState.download_info.length - 1];
                                state.speedTestState = SpeedTestStateEnum.DOWN_OK;
                                state.downBit = currentDownload.throughput_avg_bps;
                                state.downMBit = state.downBit / (1000 * 1000);
                                state.downMBit = state.downMBit.toFixed(2);
                                state.progress = 1;
                                this.$state.next(state);
                                //this.testGauge.onStateChange(state);
                                state.speedTestState = SpeedTestStateEnum.UP_PRE;
                                state.progress = 0;
                                this.$state.next(state);
                                //this.testGauge.onStateChange(state);
                                state.speedTestState = SpeedTestStateEnum.UP_PRE_OK;
                                state.progress = 0;
                            }
                            break;
                        case "upload":
                            var currentUpload;
                            if (currentState.cmd === "report") {
                                state.speedTestState = SpeedTestStateEnum.UP;
                                currentUpload = currentState.upload_info[currentState.upload_info.length - 1];
                                state.upMBit = currentUpload.throughput_avg_bps / (1000 * 1000);
                                state.upMBit = state.upMBit.toFixed(2);
                                state.upBit = currentUpload.throughput_avg_bps;
                                state.progress = (currentUpload.duration_ns / (1000 * 1000)) / downloadUploadDuration;
                            }
                            if (currentState.cmd === "finish") {
                                state.speedTestState = SpeedTestStateEnum.UP_OK;
                                 currentUpload = currentState.upload_info[currentState.upload_info.length - 1];
                                state.upMBit = currentUpload.throughput_avg_bps / (1000 * 1000);
                                state.upMBit = state.upMBit.toFixed(2);
                                state.upBit = currentUpload.throughput_avg_bps;
                                state.progress = 1;
                                this.$state.next(state);
                                //this.testGauge.onStateChange(state);
                                state.speedTestState = SpeedTestStateEnum.COMPLETE;
                                state.progress = 0;

                                this.logger.debug("Mes uuids", data);
                                state.basicState = BasicTestStateEnum.ENDED;
                                //this.testInProgress = false;
                            }
                            break;
                    }
                }
                this.$state.next(state);
                //this.testGauge.onStateChange(state);
            };
            //this.testGauge.onStateChange(state);
            this.$state.next(state);
        });
    }

    public start = (): void => {
        this.speed.measurementStart(JSON.stringify(this.config));
    }

    public destroy = (): void => {
        //
    }

}
