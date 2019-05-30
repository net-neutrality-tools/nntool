import {Injectable} from '@angular/core';

import {WebsiteSettings} from '../settings/settings.interface';
import {Logger, LoggerService} from './log.service';

import { environment } from '../../environments/environment';


export interface MapSettingsResponse {
    mapfilter: {
        mapFilters: {
            // TODO: subclass deeper
            all: any[];
            wifi: any[];
            browser: any[];
            mobile: any[];
        };
        mapTypes: {
            title: string;
            // TODO: subclass deeper
            options: any[];
        }[];
    };
}

declare var __TEST_CONFIG__: any;

@Injectable()
export class ConfigService {

    private logger: Logger = LoggerService.getLogger('ConfigService');
    private config: WebsiteSettings = environment;
    private constants: any = __TEST_CONFIG__;

    constructor() {}

    clearArray(a: any[]): void {
        while (a.length > 0) {
            a.pop();
        }
    }

    getConfig(): any {
        return environment;
    }

    getVersion(): string {
        return this.constants.version ? this.constants.version : '';
    }

    getBranch(): string {
        return this.constants.branch ? this.constants.branch : '';
    }

    getRevision(): string {
        return this.constants.revision ? this.constants.revision : '';
    }

    getClient(): string {
        return this.constants.client ? this.constants.client : '';
    }

    getServerControl(): string {
        return this.config.servers.control;
    }

    getServerStatistic(): string {
        return this.config.servers.statistic;
    }

    getServerMap(): string {
        return this.config.servers.map;
    }
}
