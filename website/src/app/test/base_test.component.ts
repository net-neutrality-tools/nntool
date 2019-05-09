import {OnInit, OnDestroy, ElementRef, NgZone, Injectable} from "@angular/core";
import {TranslateService} from "@ngx-translate/core";

import {Logger, LoggerService} from "../services/log.service";
import {ConfigService} from "../services/config.service";
import {WebsiteSettings} from "../settings/settings.interface";
import {format as formatUtils} from "../pipes/utils";
import {RequestsService} from "../services/requests.service";
import {UserInfo, UserService} from "../services/user.service";
import {ActivatedRoute, ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs";
import {AppService} from "../services/app.service";
import {TestService} from "../services/test/test.service";

@Injectable()
export class TestGuard implements CanDeactivate<BaseNetTestComponent> {

    constructor () {}

    canDeactivate(
        component: BaseNetTestComponent,
        currentRoute: ActivatedRouteSnapshot,
        currentState: RouterStateSnapshot,
        nextState: RouterStateSnapshot
    ): Observable<boolean>|Promise<boolean>|boolean {
        return !component.testInProgress;
    }
}

export abstract class BaseNetTestComponent implements OnInit, OnDestroy {

    protected logger: Logger = LoggerService.getLogger("BaseNetTestComponent");

    protected config: WebsiteSettings;
    errorMsg: string = null;
    measurementLink: string = null;
    protected _screenNr: number = 0;
    private _testInProgress: boolean = false;

    format: (value: any, setting: any) => any = formatUtils;

    get testInProgress (): boolean {
        return this._testInProgress;
    }

    set testInProgress (value: boolean) {
        if (value !== this._testInProgress) {
            if (value) {
                this.appService.disableNavigation();
            } else {
                this.appService.enableNavigation();
            }
        }
    }

    protected get screenNr (): number {
        return this._screenNr;
    }

    protected set screenNr (value: number) {
        this._screenNr = value;
    }

    get user (): UserInfo {
        return this.userService.user;
    }

    get autostart (): boolean {
        const test = typeof this.activatedRoute.snapshot.queryParams['start'];
        return typeof this.activatedRoute.snapshot.queryParams['start'] !== "undefined" && this.screenNr === 1;
    }

    constructor (
        protected testService: TestService, protected configService: ConfigService,
        protected userService: UserService, protected translateService: TranslateService,
        protected requests: RequestsService, protected elementRef: ElementRef,
        protected zone: NgZone, protected activatedRoute: ActivatedRoute, protected appService: AppService,
    ) {
    }

    ngOnInit () {
        this.config = this.configService.getConfig();
        this.screenNr = 0;
    }

    ngOnDestroy (): void {
        this.userService.save();
    }

    shouldShowInfo (): boolean {
        return this.user && this.user.acceptTC;
    }

    agree (): void {
        this.user.acceptTC = true;
        this.userService.save();
    }
}