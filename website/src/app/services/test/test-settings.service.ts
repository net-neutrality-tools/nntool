import {Injectable} from "@angular/core";

import {Logger, LoggerService} from "../log.service";
import {UserInfo, UserService} from "../user.service";
import {MeasurementAgentType} from "../../test/models/api/request-info.api";
import {TranslateService} from "@ngx-translate/core";
import {ConfigService} from "../config.service";

export interface AgentSettings {
    uuid?: string;

    // SettingsRequest
    device?: string; // Ignored on browser
    language: string;
    model?: string; // Browser - no version
    product?: string; // Browser - version?
    os_version?: string; // Ignored on browser
    previous_test_status?: string;
    timezone?: string;

    // MeasurementRequest
    anonymous?: boolean;
    test_counter?: number;

    /**
     * TODO: Duration?
     */
    test_duration?: number;
    /**
     * Remote ip
     */
    remote_ip?: string;

    /**
     * Number of threads upload/upload
     */
    test_threads?: number;

    /**
     * Number of pings during latency test
     */
    test_p?: number;

    /**
     *  Test wait to start start time
     */
    test_wait_time?: number;

    /**
     * TODO:
     */
    //test_measurement_server?: MeasurementServer;

    // Post test result

    publish_public_data?: boolean;
}

export interface TestSettings {
    servers: {
        control?: string;
        statistic?: string;
        measurement?: string;
    };
    // SettingsRequest
    agent_type: MeasurementAgentType;
    agent_name?: string;
    platform?: string;
    app_revision?: string;
    app_version?: string;
    app_version_code?: number;
    tag?: string;

    /**
     * RMBT protocol version (0.1/0.3)
     */
    app_version_name?: string;

    /**
     * TODO: Same as <software_version_name>?
     */
    version?: string;

    // MeasurementRequest
    ndt?: boolean;

    rmbt?: boolean;

    tests?: {
        qos?: boolean;
        ndt?: boolean;
        rmbt?: boolean;
    };

    qos_param?: {
        ndt: {
            use_ssl?: boolean;
        };
    };

    /**
     * Measure thread type
     */
    test_thread_type?: string;

    /**
     * Estimated speed to reduce to only 1 thread for measurements
     * in MBit per Second
     */
    test_pre_test_min_chunks?: number;

    test_pre_test_duration?: number;
    test_downlink_duration?: number;
}

@Injectable()
export class TestSettingsService {

    private static logger: Logger = LoggerService.getLogger("TestSettingsService");
    private _agentSettings: AgentSettings;
    private _testSettings: TestSettings;

    constructor(private configService: ConfigService,
                private userService: UserService,
                private translateService: TranslateService) {

        const config = this.configService.getConfig();

        this._agentSettings = {
            language: this.translateService.currentLang,
            device: null,
            model: null,
            timezone: "Etc/UTC",
            publish_public_data: true,
            anonymous: this.user.disassociated,
            // TODO: test counter
            test_counter: 0,
        };

        this._testSettings = {
            servers: config.servers,
            ndt: false,
            agent_type: MeasurementAgentType.BROWSER,
            agent_name: "RMBTws",
            platform: "RMBTws",
            // branch-commit
            app_revision: this.configService.getBranch() + "-" + this.configService.getRevision(),
            // version
            app_version: this.configService.getVersion(),
            // counter
            app_version_code: 0,
            // Protocol version
            app_version_name: "0.3",
            // browser
            test_thread_type: "SINGLE",
            test_pre_test_min_chunks: 5,
            test_pre_test_duration: 2,
            test_downlink_duration: 7,
            tag: null,

            tests: {
                qos: false,
                ndt: false,
                rmbt: true,
            },

            qos_param: {
                ndt: {
                    use_ssl: false,
                }
            },
        };
        if (config.nettest) {
            this._testSettings.tag = config.nettest.tag;

            if (config.nettest.tests) {
                this._testSettings.tests.rmbt = config.nettest.tests.rmbt;
                this._testSettings.tests.qos = config.nettest.tests.qos;
                this._testSettings.tests.ndt = config.nettest.tests.ndt;
            }
        }
    }

    private get user (): UserInfo {
        return this.userService.user;
    }

    get agentSettings(): AgentSettings {
        return this._agentSettings;
    }

    get testSettings(): TestSettings {
        return this._testSettings;
    }
}