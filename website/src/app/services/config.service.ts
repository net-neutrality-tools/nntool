import {Injectable} from '@angular/core';

import {WebsiteSettings} from "../settings/settings.interface";
import {settings} from "../settings/settings";
import {Logger, LoggerService} from "./log.service";
import * as constants from "../constants";


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


@Injectable()
export class ConfigService {

    private logger: Logger = LoggerService.getLogger("ConfigService");
    private config: WebsiteSettings = settings;


    constructor () {}

    clearArray(a: any[]): void {
        while (a.length > 0) {
            a.pop();
        }
    }

    getConfig (): any {
        return this.config;
    }

    getVersion (): string {
        return constants.version ? constants.version : "";
    }

    getBranch (): string {
        return constants.branch ? constants.branch : "";
    }

    getRevision (): string {
        return constants.revision ? constants.revision : "";
    }

    getClient (): string {
        return constants.client ? constants.client : "";
    }

    getServerControl (): string {
        return this.config.servers.control;
    }

    getServerStatistic (): string {
        return this.config.servers.statistic;
    }

    getServerMap (): string {
        return this.config.servers.map;
    }
}
