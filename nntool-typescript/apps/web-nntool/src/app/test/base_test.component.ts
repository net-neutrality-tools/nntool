import { ElementRef, Injectable, NgZone, OnDestroy, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { ActivatedRoute, ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { format as formatUtils } from '../pipes/utils';
import { AppService } from '../services/app.service';
import { ConfigService } from '../services/config.service';
import { RequestsService } from '../services/requests.service';
import { TestService } from '../services/test/test.service';
import { UserInfo, UserService } from '../services/user.service';
import { WebsiteSettings } from '../settings/settings.interface';

@Injectable()
export class TestGuard implements CanDeactivate<BaseNetTestComponent> {
  constructor() {}

  public canDeactivate(
    component: BaseNetTestComponent,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    return !component.testInProgress;
  }
}

export abstract class BaseNetTestComponent implements OnInit, OnDestroy {
  get testInProgress(): boolean {
    return this._testInProgress;
  }

  set testInProgress(value: boolean) {
    if (value !== this._testInProgress) {
      if (value) {
        this.appService.disableNavigation();
      } else {
        this.appService.enableNavigation();
      }
    }
  }

  protected get screenNr(): number {
    return this._screenNr;
  }

  protected set screenNr(value: number) {
    this._screenNr = value;
  }

  get user(): UserInfo {
    return this.userService.user;
  }

  get autostart(): boolean {
    const test = typeof this.activatedRoute.snapshot.queryParams.start;
    return typeof this.activatedRoute.snapshot.queryParams.start !== 'undefined' && this.screenNr === 1;
  }
  public errorMsg: string = null;

  public format: (value: any, setting: any) => any = formatUtils;

  protected config: WebsiteSettings;
  protected measurementLink: string = null;
  protected _screenNr = 0;
  private _testInProgress = false;

  constructor(
    protected testService: TestService,
    protected configService: ConfigService,
    protected userService: UserService,
    protected translateService: TranslateService,
    protected requests: RequestsService,
    protected elementRef: ElementRef,
    protected zone: NgZone,
    protected activatedRoute: ActivatedRoute,
    protected appService: AppService
  ) {
    this.measurementLink = null;
  }

  public ngOnInit() {
    this.config = this.configService.getConfig();
    this.screenNr = 0;
  }

  public ngOnDestroy(): void {
    this.userService.save();
  }

  public shouldShowInfo(): boolean {
    return this.user && this.user.acceptTC;
  }

  public agree(): void {
    this.user.acceptTC = true;
    this.userService.save();
  }
}
