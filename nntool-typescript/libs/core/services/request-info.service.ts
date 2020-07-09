/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { Injectable } from '@angular/core';

import { DeviceDetectorService, DeviceInfo } from 'ngx-device-detector';
import { LocationService } from './location.service';
import { TestSettingsService } from './test/test-settings.service';
import { UserService } from './user.service';
import { GeoLocation } from '../../test/models/api/request-info.api';
import { isElectron } from '@nntool-typescript/utils';

/**
 * Service to prepare the basic request info object, filled w/the correct client data
 */
@Injectable()
export class RequestInfoService {
  constructor(
    private readonly testSettingsService: TestSettingsService,
    private readonly locationService: LocationService,
    private readonly userService: UserService,
    private readonly deviceService: DeviceDetectorService
  ) { }

  public getRequestInfo(): any {
    const { agentSettings, testSettings } = this.testSettingsService;

    const deviceInfo: DeviceInfo = this.deviceService.getDeviceInfo();

    //check if we are running in electron and overwrite device settings
    if (isElectron()) {
      const win = window as any;
      deviceInfo.browser = 'Desktop';
      deviceInfo.browser_version = win.require('electron').remote.app.getVersion();
    }

    const request_info = {
      agent_type: testSettings.agent_type,
      agent_id: this.userService.user.uuid, // TODO: check which uuid is which
      api_level: undefined,
      app_git_revision: testSettings.app_revision,
      app_version_code: testSettings.app_version_code,
      app_version_name: testSettings.app_version_name,
      code_name: undefined,
      geo_location: this.getFirstLocation(),
      language: agentSettings.language,
      model: agentSettings.model,
      os_name: undefined,
      os_version: agentSettings.os_version,
      timezone: agentSettings.timezone
    };

    return request_info;
  }

  private getFirstLocation(): GeoLocation {
    const locations: GeoLocation[] = this.locationService.getLocations();
    return locations.length > 0 ? locations[0] : null;
  }
}
