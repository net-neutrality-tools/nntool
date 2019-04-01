import {TestImplementation} from "../test-implementation";
import {Logger, LoggerService} from "../../../services/log.service";
import {Injectable, NgZone} from "@angular/core";
import {Subject} from "rxjs";
import {PortBlockingTestState} from "./port-blocking-test-state";
import {BasicTestStateEnum} from "../../enums/basic-test-state.enum";
import {PortBlockingTestTypeEnum} from "./enums/port-blocking-test-type";

@Injectable({
    providedIn: 'root',
})
export class PortBlockingTestImplementation extends TestImplementation<PortBlockingTestState> {

    protected logger: Logger = LoggerService.getLogger("PortBlockingTestImplementation");
    private $state: Subject<PortBlockingTestState>;
    private config: any;
    private portBlocking: any;

    constructor(private zone: NgZone) { // TODO: Add missing services
        super();
    }

    public init = ($state: Subject<PortBlockingTestState>): void => {
        this.$state = $state;

        this.config = {
            platform: 'web',
            target: 'peer-ias-de-01.net-neutrality.tools',
            targetIpv4: '82.199.148.52',
            targetIpv6: '2a01:4a0:f::11',
            user: 'berec',
            password: 'berec',
            ports: [123, 500, 4500, 5060, 7000],
            timeout: 5000
        };

        this.portBlocking = new PortBlocking();

        this.zone.runOutsideAngular(() => {
            const state: PortBlockingTestState = new PortBlockingTestState();

            state.basicState = BasicTestStateEnum.INITIALIZED;
            state.types = [{
                key: PortBlockingTestTypeEnum.UDP,
                ports: []
            }];

            this.config.ports.forEach((port: number) => {
                state.types[0].ports.push(
                    {
                        number: port,
                        reachable: false,
                        finished: false
                    }
                );
            });

            window.measurementCallback = (data: any) => { // TODO: inject window object properly
                const currentState = JSON.parse(data);
                if (currentState.test_case === "port_blocking") {
                    switch (currentState.cmd) {
                        case "report": {
                            state.basicState = BasicTestStateEnum.RUNNING;
                            break;
                        }
                        case "completed": {
                            state.basicState = BasicTestStateEnum.ENDED;
                            break;
                        }
                    }
                    if (currentState.port_blocking && currentState.port_blocking.results) {
                        currentState.port_blocking.results.forEach((result: any) => {
                            const testedPort = result.port;
                            const reachable = result.reachable;
                            const finished = result.reachable || result.timeout;
                            state.types[0].ports.forEach((port: {
                                number: number;
                                reachable: false;
                                finished: false;
                            }) => {
                                if (port.number === testedPort) {
                                    port.reachable = reachable;
                                    port.finished = finished;
                                }
                            });
                        });
                    }
                    this.$state.next(state);
                }
            };
            this.$state.next(state);
        });
    }

    public start = (): void => {
        this.portBlocking.measurementStart(this.config);
    }

    public destroy = (): void => {
        //
    }

}