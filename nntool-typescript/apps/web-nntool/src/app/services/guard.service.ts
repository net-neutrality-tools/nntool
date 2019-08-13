import {Injectable} from '@angular/core';
import {CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {ConfigService} from './config.service';
import {FeatureSettings} from '../settings/features.settings.interface';
import {WebsiteSettings} from '../settings/settings.interface';
import {Logger, LoggerService} from './log.service';


@Injectable()
export class Guard implements CanActivate, CanActivateChild {

    private logger: Logger = LoggerService.getLogger('Guard');
    private _settings: WebsiteSettings = null;
    private _features: FeatureSettings = null;
    private _odMsg = true;

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

    constructor(private configService: ConfigService, private router: Router) {}

    private updateODMsg(url: string): void {
        this._odMsg = false;
        for (const part of ['/statistics', '/opendata', '/map', '/testresults']) {
            if (url.startsWith(part)) {
                this._odMsg = true;
                return;
            }
        }
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
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

    canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
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
                    this.router.navigate(['404'], {skipLocationChange: true});
                    return false;
                } else {
                    return true;
                }
            }
        }
        this.logger.warn('Unchecked url: ' + url);
        return true;
    }

    get hiddenInApp(): boolean {
        const agent: string = window.navigator.userAgent;
        return false; // TODO: agent && agent.toLowerCase().includes(this.settings.user_agent.toLowerCase());
    }

    get showOpenddataMessage(): boolean {
        return this._odMsg;
    }
}
