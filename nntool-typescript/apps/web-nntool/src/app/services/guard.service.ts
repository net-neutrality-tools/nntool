import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { ConfigService } from './config.service';
import { WebsiteSettings } from '../@core/models/settings/settings.interface';
import { FeatureSettings } from '../@core/models/settings/features.settings.interface';

@Injectable()
export class Guard implements CanActivate, CanActivateChild {
  get features() {
    if (this._features == null) {
      this._settings = this.configService.getConfig();
      this._features = this._settings.features;
    }
    return this._features;
  }

  get settings() {
    if (this._settings == null) {
      this._settings = this.configService.getConfig();
      this._features = this._settings.features;
    }
    return this._settings;
  }

  get hiddenInApp(): boolean {
    const agent: string = window.navigator.userAgent;
    return false; // TODO: agent && agent.toLowerCase().includes(this.settings.user_agent.toLowerCase());
  }

  get showOpenddataMessage(): boolean {
    return this._odMsg;
  }

  private _settings: WebsiteSettings = null;
  private _features: FeatureSettings = null;
  private _odMsg = true;

  constructor(private logger: NGXLogger, private configService: ConfigService, private router: Router) { }

  public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const url: string = state.url.split('?')[0];
    this.updateODMsg(url);
    if (!this.settings.landing_page) {
      return true;
    }
    if (url === '/') {
      this.router.navigate([this.settings.landing_page]);
      return false;
    }
    return true;
  }

  public canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const url: string = state.url.split('?')[0];
    this.updateODMsg(url);
    for (const key in this.features) {
      if (!this.features.hasOwnProperty(key)) {
        continue;
      }
      // TODO: starts with -> block details pages (<key>/<uuid>)
      if (url.endsWith(key)) {
        if (!this.features[key]) {
          this.logger.warn('Preventing loading:', url);
          this.router.navigate(['404'], { skipLocationChange: true });
          return false;
        } else {
          return true;
        }
      }
    }
    this.logger.warn('Unchecked url: ' + url);
    return true;
  }

  private updateODMsg(url: string): void {
    this._odMsg = false;
    for (const part of ['/statistics', '/opendata', '/map', '/testresults']) {
      if (url.startsWith(part)) {
        this._odMsg = true;
        return;
      }
    }
  }
}
