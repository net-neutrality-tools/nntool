import { Inject, Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { WINDOW } from './window.service';
import { GeoLocation } from '../../test/models/api/request-info.api';

@Injectable()
export class LocationService {
  private positions: GeoLocation[] = [];
  private watchId: number = null;
  private running = false;
  private navigator: Navigator = null;

  constructor(private logger: NGXLogger, @Inject(WINDOW) private window: Window) {
    if (this.window) {
      this.navigator = this.window.navigator;
    }
  }

  public startTracking(options?: PositionOptions): void {
    this.positions = [];

    if (this.navigator && this.navigator.geolocation) {
      this.navigator.geolocation.getCurrentPosition(
        (loc: Position) => {
          this.addLocation(loc);
          this.running = true;
          this.watchId = this.navigator.geolocation.watchPosition(
            (loc2: Position) => {
              this.addLocation(loc2);
            },
            this.locationError,
            options
          );
        },
        this.locationError,
        options
      );
    } else {
      console.error('Geolocation is not supported');
    }
  }

  public stopTracking(): void {
    if (this.running && this.navigator) {
      this.navigator.geolocation.clearWatch(this.watchId);
      this.running = false;
    }
  }

  public getLocations(): GeoLocation[] {
    return this.positions;
  }

  private locationError(error: PositionError): void {
    switch (error.code) {
      case error.PERMISSION_DENIED:
        //this.logger.error('User denied location service');
        break;
      case error.POSITION_UNAVAILABLE:
        //this.logger.error('Position is not available');
        break;
      case error.TIMEOUT:
        //this.logger.error('Timeout while position request');
        break;
      default:
        break;
    }
  }

  private addLocation(loc: Position): void {
    this.logger.debug('Adding', loc);
    this.positions.push({
      time: new Date().toJSON().slice(0, -1),
      latitude: loc.coords.latitude,
      longitude: loc.coords.longitude,
      accuracy: loc.coords.accuracy,
      altitude: loc.coords.altitude,
      heading: loc.coords.heading,
      speed: loc.coords.speed,
      provider: 'BROWSER',
      relative_time_ns: this.window && this.window.performance ? this.window.performance.now() : null
    });
    if (!this.running && this.watchId !== null) {
      navigator.geolocation.clearWatch(this.watchId);
    }
  }
}
