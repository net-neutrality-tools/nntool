import {Component, OnDestroy, AfterViewInit, ElementRef, Renderer2} from "@angular/core";

import {TranslateService, LangChangeEvent} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {finalize} from "rxjs/operators";

import {ADocService} from "./adoc.service";
import {LoggerService} from "../services/log.service";
import {ConfigService} from "../services/config.service";


@Component({
    templateUrl: "./adoc.component.html"
})
export class ADocComponent implements AfterViewInit, OnDestroy {

    protected logger = LoggerService.getLogger("ADocComponent");
    key: string = "";
    private subs: Subscription[] = [];
    loading: boolean = false;
    protected injectedVars: {[key: string]: string};


    constructor (
        private elementRef: ElementRef, private renderer: Renderer2,
        private translateService: TranslateService,
        private adoc: ADocService, protected configService: ConfigService,
    ) {}

    ngOnInit () {
        this.loading = true;
        this.adoc.setPage(this.key)
            .pipe( finalize(() => { this.loading = false; }) )
            .subscribe(
                () => { this.inject(this.injectedVars); },
                (err: any) => {
                    this.logger.error('Adoc setPage', err);
                }
            );
    }

    ngAfterViewInit () {
        this.subs.push(this.translateService.onLangChange.subscribe(
            (event: LangChangeEvent) => {
                if (!this.loading || this.translateService.currentLang !== event.lang) {
                    this.loading = true;
                    this.adoc.setPage(this.key, event.lang)
                        .pipe( finalize( () => { this.loading = false; }) )
                        .subscribe(
                            () => { this.inject(this.injectedVars); },
                            (err: any) => {
                                this.logger.error('Adoc setPage', err);
                            }
                        );
                }
            }
        ));
    }

    ngOnDestroy(): void {
        while (this.subs.length > 0) {
            try {
                this.subs.pop().unsubscribe();
            } catch (e) {
                this.logger.error("Failed to unsubscribe", e);
            }
        }
    }

    private inject (vars: {[key: string]: string}): void {
        setTimeout(() => {
            for (const key in vars) {
                if (!vars.hasOwnProperty(key)) {
                    continue;
                }
                this.logger.debug(key, vars[key]);
                while (true) {
                    let el: any;
                    try {
                        el = this.renderer.selectRootElement('.' + key);
                        this.renderer.appendChild(el, this.renderer.createText(vars[key]));
                        this.renderer.removeClass(el, key);
                        this.renderer.addClass(el, 'injected-' + key);
                    } catch (e) {
                        // Not found
                        break;
                    }
                }
            }
        }, 0);
    }
}