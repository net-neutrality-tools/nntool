import {Injectable} from '@angular/core';

import {Logger, LoggerService} from './log.service';
import {TestSettingsService} from './test/test-settings.service';
import {GeoLocation} from '../test/models/api/request-info.api';
import {LocationService} from './location.service';
import {UserService} from './user.service';
import {DeviceDetectorService, DeviceInfo} from 'ngx-device-detector'

/**
 * Service to prepare the basic request info object, filled w/the correct client data
 */
@Injectable()
export class RequestInfoService {

    private static logger: Logger = LoggerService.getLogger('RequestInfoService');

    constructor (private readonly testSettingsService: TestSettingsService, 
                private readonly locationService: LocationService,
                private readonly userService: UserService,
                private readonly deviceService: DeviceDetectorService) {

    }

    getRequestInfo(): any {
        
        const { agentSettings, testSettings } = this.testSettingsService;

        const deviceInfo: DeviceInfo = this.deviceService.getDeviceInfo();
        
        let request_info = {
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
