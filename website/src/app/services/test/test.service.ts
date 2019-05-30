import {Injectable} from "@angular/core";

import {Observable, of} from "rxjs";
import {map, mergeMap, tap} from "rxjs/operators";

import {Logger, LoggerService} from "../log.service";
import {UserInfo, UserService} from "../user.service";
import {RequestAPI} from "../../test/models/api/request.api";
import {RegistrationRequestAPI} from "../../test/models/registration/registration-request.api";
import {ResponseAPI} from "../../test/models/api/response.api";
import {RegistrationResponseAPI} from "../../test/models/registration/registration-response.api";
import {MeasurementAgentType} from "../../test/models/api/request-info.api";
import {RequestsService} from "../requests.service";
import {SettingsRequestAPI} from "../../test/models/settings/settings-request.api";
import {SettingsResponseAPI} from "../../test/models/settings/settings-response.api";
import {LmapControlAPI} from "../../test/models/measurements/lmap-control.api";
import {LmapReportAPI} from "../../test/models/measurements/lmap-report.api";
import {MeasurementResultResponseAPI} from "../../test/models/measurements/measurement-result-response.api";
import {TestSettingsService} from "./test-settings.service";
import {ConfigService} from "../config.service";


@Injectable()
export class TestService {

    private settings: SettingsResponseAPI;

    private static logger: Logger = LoggerService.getLogger("TestService");


    constructor (private requestService: RequestsService,
                 private userService: UserService,
                 private testSettingsService: TestSettingsService,
                 private configService: ConfigService) {
    }

    private postMeasurementAgent (registrationRequest?: RequestAPI<RegistrationRequestAPI>):
        Observable<ResponseAPI<RegistrationResponseAPI>> {

        // default values
        if (!registrationRequest) {

            const { agentSettings, testSettings } = this.testSettingsService;

            registrationRequest = {
                data: {
                    group_name: undefined, // wurst
                    terms_and_conditions_accepted: true,
                    terms_and_conditions_accepted_version: 12
                },
                request_info: {
                    agent_type: testSettings.agent_type,
                    api_level: undefined,
                    app_git_revision: testSettings.app_revision,
                    app_version_code: testSettings.app_version_code,
                    app_version_name: testSettings.app_version_name,
                    code_name: undefined,
                    geo_location: { // TODO: Real data
                        accuracy: 0,
                        altitude: 0,
                        heading: 0,
                        latitude: 0,
                        longitude: 0,
                        provider: undefined,
                        relative_time_ns: 0,
                        speed: 0,
                        time: new Date().toISOString().slice(0, -1)
                    },
                    language: agentSettings.language,
                    model: agentSettings.model,
                    os_name: undefined,
                    os_version: agentSettings.os_version,
                    timezone: agentSettings.timezone
                }
            };
        }

        return this.requestService.postJson<ResponseAPI<RegistrationResponseAPI>>(
            `${this.configService.getServerControl()}measurement-agents`, registrationRequest);
    }

    private getSettings (settingsRequest?: RequestAPI<SettingsRequestAPI>): Observable<ResponseAPI<SettingsResponseAPI>> {

        // default values
        if (!settingsRequest) {

            const { agentSettings, testSettings } = this.testSettingsService;
            settingsRequest = {
                data: {
                },
                request_info: {
                    agent_type: MeasurementAgentType.MOBILE,
                    agent_id: this.userService.user.uuid, // TODO: check which uuid is which
                    api_level: undefined,
                    app_git_revision: testSettings.app_revision,
                    app_version_code: testSettings.app_version_code,
                    app_version_name: testSettings.app_version_name,
                    code_name: undefined,
                    geo_location: {
                        accuracy: 0,
                        altitude: 0,
                        heading: 0,
                        latitude: 0,
                        longitude: 0,
                        provider: undefined,
                        relative_time_ns: 0,
                        speed: 0,
                        time: new Date().toISOString().slice(0, -1)
                    },
                    language: agentSettings.language,
                    model: agentSettings.model,
                    os_name: undefined,
                    os_version: agentSettings.os_version,
                    timezone: agentSettings.timezone
                }
            };
        }

        return this.requestService.getJson<ResponseAPI<SettingsResponseAPI>>(
            `${this.configService.getServerControl()}measurement-agents/
            ${settingsRequest.request_info.agent_id}/settings`, settingsRequest).pipe(
            tap((response: ResponseAPI<SettingsResponseAPI>) => {
                this.settings = response.data;
            })
        );
    }

    public newMeasurement (lmapControl?: LmapControlAPI): Observable<LmapControlAPI> {

        return of(this.userService.user).pipe(
            mergeMap((user: UserInfo) => {
                if (!user.uuid) {
                    return this.postMeasurementAgent().pipe(
                        map((response: ResponseAPI<RegistrationResponseAPI>) => {
                            user.uuid = response.data.agent_uuid;
                            this.userService.save(user);
                            return user;
                        })
                    );
                } else {
                    return of(user);
                }
            }),
            mergeMap((user: UserInfo) => {
                return this.getSettings().pipe(
                    map((response: ResponseAPI<SettingsResponseAPI>) => {
                        return {user: user, settings: response.data};
                    })
                );
            }),
            mergeMap((context: {user: UserInfo, settings: SettingsResponseAPI}) => {
                const { agentSettings, testSettings } = this.testSettingsService;

                if (!lmapControl) {
                    lmapControl = {
                        "additional-request-info": undefined,
                        agent: {
                            "agent-id": context.user.uuid,
                            "group-id": undefined,
                            "measurement-point": undefined,
                            "report-agent-id": undefined,
                            "report-group-id": undefined,
                            "report-measurement-point": undefined,
                            "controller-timeout": undefined,
                            "last-started": undefined
                        },
                        capabilities: {
                            tasks: [
                                {
                                    name: "SPEED",
                                    version: "1.0.0",
                                    function: undefined,
                                    program: undefined
                                },
                                {
                                    name: "QOS",
                                    version: "1.0.0",
                                    function: undefined,
                                    program: undefined
                                }
                            ],
                            version: undefined,
                            tag: undefined
                        },
                        "events": undefined,
                        "schedules": undefined,
                        "suppressions": undefined,
                        "tasks": undefined
                    };
                }
                lmapControl["additional-request-info"] = {
                    agent_type: testSettings.agent_type,
                    agent_uuid: context.user.uuid,
                    api_level: undefined,
                    app_git_revision: testSettings.app_revision,
                    app_version_code: testSettings.app_version_code,
                    app_version_name: testSettings.app_version_name,
                    code_name: undefined,
                    geo_location: { // TODO: Real data
                        accuracy: 0,
                        altitude: 0,
                        heading: 0,
                        latitude: 0,
                        longitude: 0,
                        provider: undefined,
                        relative_time_ns: 0,
                        speed: 0,
                        time: new Date().toISOString().slice(0, -1)
                    },
                    language: agentSettings.language,
                    model: agentSettings.model,
                    os_name: undefined,
                    os_version: agentSettings.os_version,
                    timezone: agentSettings.timezone
                };

                return this.requestService.postJson<LmapControlAPI>(
                    `${this.configService.getServerControl()}measurements`, lmapControl);
            })
        );
    }

    public postMeasurementResults (lmapReport: LmapReportAPI, serverCollectorUrl: string):
        Observable<ResponseAPI<MeasurementResultResponseAPI>> {
        const { agentSettings, testSettings } = this.testSettingsService;

        if (!lmapReport) {
            lmapReport = {
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
        }

        lmapReport["additional_request_info"] = {
            agent_type: testSettings.agent_type,
            agent_uuid: this.userService.user.uuid,
            api_level: undefined,
            app_git_revision: testSettings.app_revision,
            app_version_code: testSettings.app_version_code,
            app_version_name: testSettings.app_version_name,
            code_name: undefined,
            geo_location: { // TODO: Real data
                accuracy: 0,
                altitude: 0,
                heading: 0,
                latitude: 0,
                longitude: 0,
                provider: undefined,
                relative_time_ns: 0,
                speed: 0,
                time: new Date().toISOString().slice(0, -1)
            },
            language: agentSettings.language,
            model: agentSettings.model,
            os_name: undefined,
            os_version: agentSettings.os_version,
            timezone: agentSettings.timezone
        };
        lmapReport["agent-id"] = this.userService.user.uuid;

        if (serverCollectorUrl === undefined || serverCollectorUrl === null || serverCollectorUrl === '') {
            serverCollectorUrl = this.settings.urls.collector_service;
        }

        return this.requestService.postJson<ResponseAPI<MeasurementResultResponseAPI>>(
            `${serverCollectorUrl}`, lmapReport);
    }
}