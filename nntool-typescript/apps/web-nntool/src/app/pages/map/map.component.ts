import { AfterViewInit, Component, OnInit } from '@angular/core';
declare let google: any;

export interface MarkerInfoResponse {
  measurements: Array<{
    geo_lat: number;
    geo_long: number;
    lat: number;
    lon: number;
    time_string: string;
    open_test_uuid: string;
    measurement: any[];
    net: any[];
  }>;
}

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html'
})
export class MapComponent {}
