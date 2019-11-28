import { Injectable } from '@angular/core';
import { RequestsService } from './requests.service';
import { ConfigService } from './config.service';
import { Observable } from 'rxjs';
import { ResultGroupItem } from '../models/result.groups';

export interface MapSettingsResponse {
  mapfilter: {
    mapFilters: {
      // TODO: subclass deeper
      all: any[];
      wifi: any[];
      browser: any[];
      mobile: any[];
    };
    mapTypes: Array<{
      title: string;
      // TODO: subclass deeper
      options: any[];
    }>;
  };
}

export interface MarkerInfoResponse {
  measurements: Array<{
    result_items: Array<ResultGroupItem>,
    geo_lat: number;
    geo_long: number;
    time_stamp: number;
    open_test_uuid: string;
    measurement: any[];
    net: any[];
  }>;
}

interface MarkerInfoRequest {
  coordinates: {
    lat: number,
    lon: number,
    x?: number,
    y?: number,
    zoom: number
  };
  filter?: Map<string, string>;
}

@Injectable()
export class MapService {

  private mapUrl: string;

  constructor(
    private requestService: RequestsService,
    configService: ConfigService
  ) {
    this.mapUrl = configService.getServerMap();
  }

  public getMarkerInfo(lat: number, lon: number, zoom: number, filter?: Map<string, string> ) : Observable<MarkerInfoResponse> {
    const req: MarkerInfoRequest = {coordinates: {
        lat: lat,
        lon: lon,
        zoom: zoom
        },
      filter: filter};
    return this.requestService.postJson(this.mapUrl + "tiles/markers", req);
  }

}
