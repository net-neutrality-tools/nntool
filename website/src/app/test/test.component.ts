import {Component, ElementRef, Injectable, NgZone} from "@angular/core";
import {ActivatedRoute} from "@angular/router";

import {TranslateService} from "@ngx-translate/core";
import {BaseNetTestComponent} from "./base_test.component";
import {LoggerService} from "../services/log.service";
import {ConfigService} from "../services/config.service";
import {UserService} from "../services/user.service";
import {RequestsService} from "../services/requests.service";
import {AppService} from "../services/app.service";


export {TestGuard} from "./test.guard";


@Component({
    templateUrl: "./app/test/test.component.html"
})
export class NetTestComponent extends BaseNetTestComponent {

    currentIndex = 0;

    constructor (
        configService: ConfigService, userService: UserService,
        translateService: TranslateService,
        requests: RequestsService, elementRef: ElementRef,
        zone: NgZone, activatedRoute: ActivatedRoute, appService: AppService,
    ) {
        super(configService, userService, translateService, requests, elementRef, zone, activatedRoute, appService);
        this.logger = LoggerService.getLogger("NetTestComponent");
    }

    ngOnInit () {
        super.ngOnInit();
        this.screenNr = 1;
    }

    agree (): void {
        super.agree();
    }

    setCurrentIndex(event: number) {
        this.currentIndex = event;
    }
}