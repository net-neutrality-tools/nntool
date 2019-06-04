import {Injectable} from '@angular/core';


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
export class MapService {

}
