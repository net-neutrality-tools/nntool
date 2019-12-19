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
