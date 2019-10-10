import { Component, Input, NgZone } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-bing-layer';
import { NGXLogger } from 'ngx-logger';
import { ConfigService } from '../../@core/services/config.service';
import { RequestsService } from '../../@core/services/requests.service';
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
export class MapComponent {

  private maxZoom: number = 12;

  private currentZoom: number;

  @Input()
  private options = {
    zoom: 5,
    center: L.latLng(46.879966, 12.726909)
  };

  private baseAerialLayer: any = (L as any).tileLayer.bing({bingMapsKey: "ENTER_BING_MAPS_KEY", maxZoom: 18, attribution: '...', imagerySet: "AerialWithLabelsOnDemand" });
  private baseRoadLayer: any = (L as any).tileLayer.bing({bingMapsKey: "ENTER_BING_MAPS_KEY", imagerySet: "RoadOnDemand", maxZoom: 18, attribution: '...' });
  private baseCanvasLayer: any = (L as any).tileLayer.bing({bingMapsKey: "ENTER_BING_MAPS_KEY", imagerySet: "CanvasLight", maxZoom: 18, attribution: '...' });
  private baseCanvasGrayLayer: any = (L as any).tileLayer.bing({bingMapsKey: "ENTER_BING_MAPS_KEY", imagerySet: "CanvasGray", maxZoom: 18, attribution: '...' });
  private heatmapOverlay: L.TileLayer; 
  private pointmapOverlay: L.TileLayer;

  private baseMaps;
  private overlayMaps;

  constructor(
    configService: ConfigService,
    private logger: NGXLogger,
    private zone: NgZone
    ) {
      this.currentZoom = this.options.zoom;

      let mapUrl: string = configService.getServerMap();
      this.heatmapOverlay = L.tileLayer(mapUrl + '/api/v0/tiles/heatmap?path={z}/{x}/{y}&point_diameter=12&size=256');
      this.pointmapOverlay = L.tileLayer(mapUrl + '/api/v0/tiles/points?path={z}/{x}/{y}&point_diameter=12&size=256');

      this.baseMaps = {
        "Grayscale view": this.baseCanvasGrayLayer,
        "Canvas view": this.baseCanvasLayer,
        "Aerial view": this.baseAerialLayer,
        "Road view": this.baseRoadLayer
      }

      this.overlayMaps = {
        "Heatmap": this.heatmapOverlay,
        "Points": this.pointmapOverlay
      }

    }

  onMapReady(map: L.Map) {
    L.control.layers(this.baseMaps, this.overlayMaps ,{position: 'bottomright', collapsed: false}).addTo(map);
    
    map.on("zoomend", (event) => {
        this.zone.run( () => {
          this.currentZoom = event.target._zoom;
        });
    }, this);

    //refuse to add points overlay if zoom is too far out
    map.on("overlayadd", (event: L.LayerEvent) => {
      this.logger.info(event);
      console.log(event);
      if ((event as any).name === "Points" && !this.isPointmapShown()) {
        map.removeLayer(this.pointmapOverlay);
        return;
      }
    }, this);
  }
  
  public isPointmapShown() {
    return this.currentZoom > this.maxZoom;
  }

}
