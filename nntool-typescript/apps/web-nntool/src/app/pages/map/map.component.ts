import { Component, Input, NgZone } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-bing-layer';
import { NGXLogger } from 'ngx-logger';
import { ConfigService, MapViewSettings } from '../../@core/services/config.service';
import { MapService } from '../../@core/services/map.service';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html'
})
export class MapComponent {

  private maxZoom: number = 12;

  private currentZoom: number;

  @Input()
  private options = {};

  private baseAerialKey: string = "MAP.SELECT.LAYER.AERIAL";
  private baseRoadKey: string = "MAP.SELECT.LAYER.ROAD";
  private baseCanvasKey: string = "MAP.SELECT.LAYER.CANVAS";
  private baseCanvasGrayKey: string = "MAP.SELECT.LAYER.CANVAS_GRAYSCALE";
  private heatmapOverlayKey: string = "MAP.SELECT.LAYER.HEATMAP";
  private pointmapOverlayKey: string = "MAP.SELECT.LAYER.POINTS";

  private baseAerialLayer: L.TileLayer;
  private baseRoadLayer: L.TileLayer;
  private baseCanvasLayer: L.TileLayer;
  private baseCanvasGrayLayer: L.TileLayer;
  private heatmapOverlay: L.TileLayer; 
  private pointmapOverlay: L.TileLayer;

  private baseMaps: L.Control.LayersObject;
  private overlayMaps: L.Control.LayersObject;

  private map: L.Map;

  constructor(
    configService: ConfigService,
    private mapService: MapService,
    private router: Router,
    private logger: NGXLogger,
    private zone: NgZone,
    private translateService: TranslateService
    ) {
      const mapUrl: string = configService.getServerMap();
      const bingMapKey: string = configService.getBingKey();
      this.heatmapOverlay = L.tileLayer(mapUrl + 'tiles/heatmap?path={z}/{x}/{y}&point_diameter=12&size=256');
      this.pointmapOverlay = L.tileLayer(mapUrl + 'tiles/points?path={z}/{x}/{y}&point_diameter=12&size=256');

      this.baseAerialLayer = (L as any).tileLayer.bing({bingMapsKey: bingMapKey, imagerySet: "AerialWithLabelsOnDemand", maxZoom: 18 });
      this.baseRoadLayer = (L as any).tileLayer.bing({bingMapsKey: bingMapKey, imagerySet: "RoadOnDemand", maxZoom: 18 });
      this.baseCanvasLayer = (L as any).tileLayer.bing({bingMapsKey: bingMapKey, imagerySet: "CanvasLight", maxZoom: 18 });
      this.baseCanvasGrayLayer = (L as any).tileLayer.bing({bingMapsKey: bingMapKey, imagerySet: "CanvasGray", maxZoom: 18 });

      this.translateService.get([this.baseAerialKey, this.baseRoadKey, this.baseCanvasKey, this.baseCanvasGrayKey, this.heatmapOverlayKey, this.pointmapOverlayKey]).subscribe(translated => {
        this.baseMaps = {};
        this.overlayMaps = {};
        /**
         * Unfortunately, the default layer is set by being the only base layer added to the html (see: map.component.html)
         */
        this.baseMaps[translated[this.baseCanvasGrayKey]] = this.baseCanvasGrayLayer;
        this.baseMaps[translated[this.baseCanvasKey]] = this.baseCanvasLayer;
        this.baseMaps[translated[this.baseAerialKey]] = this.baseAerialLayer;
        this.baseMaps[translated[this.baseRoadKey]] = this.baseRoadLayer;

        this.overlayMaps[translated[this.heatmapOverlayKey]] = this.heatmapOverlay;
        this.overlayMaps[translated[this.pointmapOverlayKey]] = this.pointmapOverlay;

        if (this.map) {
          L.control.layers(this.baseMaps, this.overlayMaps ,{position: 'bottomright', collapsed: false}).addTo(this.map);
        }
      });

      const mapSettings: MapViewSettings = configService.getMapViewSettings();
      this.options = {
        zoom: mapSettings.zoom_initial,
        center: new L.LatLng(mapSettings.position[0], mapSettings.position[1]),
        zoomControl: false
      }

      this.currentZoom = mapSettings.zoom_initial;

    }

  onMapReady(map: L.Map) {
    this.map = map;
    if (this.baseMaps) {
      L.control.layers(this.baseMaps, this.overlayMaps ,{position: 'bottomright', collapsed: false}).addTo(map);
    }
    
    map.on("zoomend", (event) => {
        this.zone.run( () => {
          this.currentZoom = event.target._zoom;
        });
    }, this);

    //refuse to add points overlay if zoom is too far out
    map.on("overlayadd", (event: L.LayerEvent) => {
      if ((event as any).name === "Points" && !this.isPointmapShown()) {
        map.removeLayer(this.pointmapOverlay);
        return;
      }
    }, this);

    map.on("click", (event: L.LeafletMouseEvent) => {
      if (this.isPointmapShown) {
        this.mapService.getMarkerInfo(event.latlng.lat, event.latlng.lng, this.currentZoom).subscribe( 
          res => {
            if (res.measurements.length > 0) {

              this.translateService.get(["MAP.MARKER.MEASUREMENTS", "MAP.MARKER.MORE"]).subscribe(translated => {
                let popupContent: string = translated["MAP.MARKER.MEASUREMENTS"] + "<br/>";
                res.measurements.forEach(element => {
                  popupContent += "<br/>"
                  element.result_items.forEach(item => {
                    popupContent += item.title + ": " + item.value + "<br/>";
                  });
                  const measurementUrl = this.router.createUrlTree(['/open-data-results', element.open_test_uuid]).toString();
                  popupContent += '<a href="' + measurementUrl + '">' + translated["MAP.MARKER.MORE"] + '</a>';
                  popupContent += '<br/> <hr/>'
                  element.open_test_uuid;
                });

                L.popup()
                .setLatLng(L.latLng(res.measurements[0].geo_lat, res.measurements[0].geo_long))
                .setContent(popupContent)
                .openOn(map);

              });
              
            }
        });
      }
    }, this);

  }
  
  public isPointmapShown() {
    return this.currentZoom > this.maxZoom;
  }

}