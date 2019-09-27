import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';
import { map, mergeMap, tap } from 'rxjs/operators';

import { DeviceDetectorService, DeviceInfo } from 'ngx-device-detector';
import { ConfigService } from '../config.service';
import { LocationService } from '../location.service';
import { RequestInfoService } from '../request-info.service';
import { RequestsService } from '../requests.service';
import { UserInfo, UserService } from '../user.service';
import { TestSettingsService } from './test-settings.service';
import { SettingsResponseAPI } from '../../../test/models/settings/settings-response.api';
import { SpeedMeasurementPeer } from '../../../test/models/server-selection/speed-measurement-peer';
import { LmapControl } from '../../models/lmap/models/lmap-control.model';
import { RegistrationResponseAPI } from '../../../test/models/registration/registration-response.api';
import { ResponseAPI } from '../../../test/models/api/response.api';
import { LmapReport } from '../../models/lmap/models/lmap-report.model';
import { MeasurementResultResponseAPI } from '../../../test/models/measurements/measurement-result-response.api';
import { RegistrationRequestAPI } from '../../../test/models/registration/registration-request.api';
import { RequestAPI } from '../../../test/models/api/request.api';
import { SettingsRequestAPI } from '../../../test/models/settings/settings-request.api';
import { GeoLocation } from '../../../test/models/api/request-info.api';
import { environment } from '../../../../environments/environment';

@Injectable()
export class TestService {
  private settings: SettingsResponseAPI;

  constructor(
    private requestService: RequestsService,
    private userService: UserService,
    private testSettingsService: TestSettingsService,
    private configService: ConfigService,
    private locationService: LocationService,
    private deviceService: DeviceDetectorService,
    private requestInfoService: RequestInfoService
  ) { }

  public newMeasurement(
    lmapControl?: LmapControl,
    speedMeasurementPeer?: SpeedMeasurementPeer
  ): Observable<LmapControl> {
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
            return { user, settings: response.data };
          })
        );
      }),
      mergeMap((context: { user: UserInfo; settings: SettingsResponseAPI }) => {
        const { agentSettings, testSettings } = this.testSettingsService;

        if (!lmapControl) {
          lmapControl = {
            'additional-request-info': undefined,
            agent: {
              'agent-id': context.user.uuid,
              'group-id': undefined,
              'measurement-point': undefined,
              'report-agent-id': undefined,
              'report-group-id': undefined,
              'report-measurement-point': undefined,
              'controller-timeout': undefined,
              'last-started': undefined
            },
            capabilities: {
              tasks: [
                {
                  name: 'SPEED',
                  version: '1.0.0',
                  function: undefined,
                  program: undefined
                },
                {
                  name: 'QOS',
                  version: '1.0.0',
                  function: undefined,
                  program: undefined
                }
              ],
              version: undefined,
              tag: undefined
            },
            events: undefined,
            schedules: undefined,
            suppressions: undefined,
            tasks: undefined
          };
        }

        if (speedMeasurementPeer) {
          lmapControl.capabilities.tasks.some(value => {
            if (value.name === 'SPEED') {
              value.selected_measurement_peer_identifier = speedMeasurementPeer.identifier;
              return true;
            }
            return false;
          });
        }

        const deviceInfo: DeviceInfo = this.deviceService.getDeviceInfo();

        const requestInfo = this.requestInfoService.getRequestInfo();
        requestInfo.agent_uuid = context.user.uuid;
        requestInfo.code_name = deviceInfo.browser_version;
        requestInfo.model = deviceInfo.browser;
        requestInfo.os_name = deviceInfo.os;
        requestInfo.os_version = deviceInfo.os_version;

        lmapControl['additional-request-info'] = requestInfo;

        return this.requestService.postJson<LmapControl>(
          `${this.configService.getServerControl()}measurements`,
          lmapControl
        );
      })
    );
  }

  public postMeasurementResults(
    lmapReport: LmapReport,
    serverCollectorUrl: string
  ): Observable<ResponseAPI<MeasurementResultResponseAPI>> {
    const { agentSettings, testSettings } = this.testSettingsService;

    if (!lmapReport) {
      lmapReport = {
        additional_request_info: undefined,
        'agent-id': undefined,
        date: undefined,
        'group-id': undefined,
        'measurement-point': undefined,
        result: [
          {
            action: undefined,
            conflict: undefined,
            'cycle-number': undefined,
            end: undefined,
            event: undefined,
            option: undefined,
            parameters: undefined,
            results: [],
            schedule: undefined,
            start: undefined,
            status: undefined,
            tag: undefined,
            task: undefined
          }
        ],
        time_based_result: undefined
      };
    }

    const deviceInfo: DeviceInfo = this.deviceService.getDeviceInfo();

    const requestInfo = this.requestInfoService.getRequestInfo();
    requestInfo.agent_uuid = this.userService.user.uuid;
    requestInfo.code_name = deviceInfo.browser_version;
    requestInfo.model = deviceInfo.browser;
    requestInfo.os_name = deviceInfo.os;
    requestInfo.os_version = deviceInfo.os_version;

    lmapReport.additional_request_info = requestInfo;
    lmapReport['agent-id'] = this.userService.user.uuid;

    if (serverCollectorUrl === undefined || serverCollectorUrl === null || serverCollectorUrl === '') {
      serverCollectorUrl = this.settings.urls.collector_service;
    }

    return this.requestService.postJson<ResponseAPI<MeasurementResultResponseAPI>>(`${serverCollectorUrl}`, lmapReport);
  }

  private postMeasurementAgent(
    registrationRequest?: RequestAPI<RegistrationRequestAPI>
  ): Observable<ResponseAPI<RegistrationResponseAPI>> {
    // default values
    if (!registrationRequest) {
      const { agentSettings, testSettings } = this.testSettingsService;

      registrationRequest = {
        data: {
          deserialize_type: environment.deserializeTypes.registrationRequestDeserializeType,
          group_name: undefined,
          terms_and_conditions_accepted: true,
          terms_and_conditions_accepted_version: 12
        },
        request_info: this.requestInfoService.getRequestInfo()
      };
    }

    return this.requestService.postJson<ResponseAPI<RegistrationResponseAPI>>(
      `${this.configService.getServerControl()}measurement-agents`,
      registrationRequest
    );
  }

  private getSettings(settingsRequest?: RequestAPI<SettingsRequestAPI>): Observable<ResponseAPI<SettingsResponseAPI>> {
    // default values
    if (!settingsRequest) {
      const { agentSettings, testSettings } = this.testSettingsService;

      settingsRequest = {
        data: {
          deserialize_type: environment.deserializeTypes.settingsRequestDeserializeType
        },
        request_info: this.requestInfoService.getRequestInfo()
      };
    }

    return this.requestService
      .getJson<ResponseAPI<SettingsResponseAPI>>(
        `${this.configService.getServerControl()}measurement-agents/
            ${settingsRequest.request_info.agent_id}/settings`,
        settingsRequest
      )
      .pipe(
        tap((response: ResponseAPI<SettingsResponseAPI>) => {
          this.settings = response.data;
        })
      );
  }

  private getFirstLocation(): GeoLocation {
    const locations: GeoLocation[] = this.locationService.getLocations();
    return locations.length > 0 ? locations[0] : null;
  }
}
