import {Component, OnInit, AfterViewInit, ElementRef, OnDestroy} from '@angular/core';
import {ConfigService} from '../services/config.service';
import {RequestsService} from '../services/requests.service';
import {WebsiteSettings} from '../settings/settings.interface';
import {ActivatedRoute, Router} from '@angular/router';
import {Logger} from '../services/log.service';
import {LoggerService} from '../services/log.service';
import {NumberFormatPipe} from '../pipes/number.format.pipe';
import {SpeedFormatPipe} from '../pipes/speed.format.pipe';
import {PingFormatPipe} from '../pipes/ping.format.pipe';
import {TranslateService} from '@ngx-translate/core';
import {Subscription} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {UserInfo} from '../services/user.service';
import {DecimalPipe} from '@angular/common';
import {RoundPipe} from '../pipes/round.pipe';
import {FixedFormatPipe} from '../pipes/fixed.format.pipe';


@Component({
    templateUrl: './result_list.component.html'
})
export class ResultListComponent implements OnInit, AfterViewInit, OnDestroy {

    protected elementRef: ElementRef;
    protected router: Router;
    protected configService: ConfigService;
    requests: RequestsService;
    protected translationService: TranslateService;
    protected activatedRoute: ActivatedRoute;
    protected logger: Logger = LoggerService.getLogger('ResultListComponent');
    protected config: WebsiteSettings;

    results: {
        time: string;
        name: string;
        down: string;
        up: string;
        ping: string;
        signal: string;
        uuid: string;
    }[] = [];
    cursor: number;
    loading = true;
    translationKey = 'RESULT_LIST';
    urlpath = '/testresults/';
    protected subs: Subscription[] = [];
    protected queryParams: any = {};
    protected rmbtwsTrans: string = null;
    graph: any = null;

    get user(): UserInfo {
        return null;
    }

    constructor(
        elementRef: ElementRef, router: Router,
        configService: ConfigService, requests: RequestsService, translationService: TranslateService,
        activatedRoute: ActivatedRoute
    ) {
        this.elementRef = elementRef;
        this.router = router;
        this.configService = configService;
        this.requests = requests;
        this.translationService = translationService;
        this.activatedRoute = activatedRoute;
    }

    ngOnInit() {
        this.config = this.configService.getConfig();
        this.subs.push(this.translationService.onLangChange.subscribe(() => {
            this.langChange();
        }));
        this.subs.push(this.activatedRoute.queryParamMap.subscribe(
            (data: any) => {
                this.logger.debug('Query param', data);
                // TODO: clean/validate
                this.queryParams = data.params;
            }
        ));
        this.subs.push(this.translationService.get('RESULT_LIST.RMBTWS').subscribe((data: string) => {
            this.rmbtwsTrans = data;
        }));
        this.loading = false;
        this.search();
    }

    ngAfterViewInit() {
        // TODO: stacktable
        /*if ($ === undefined) {
            this.logger.error("jQuery not loaded");
            return;
        }
        if ((<any>$.fn).stackable === undefined) {
            this.requests.getScript(
                "assets/js/stacktable.min.js", this.elementRef,
                () => {
                    let results: any = $('#results');
                    results.stacktable();
                }
            );
        } else {
            this.logger.debug("Stackable already loaded");
            let results: any = $('#results');
            results.stacktable();
        }*/
    }

    ngOnDestroy(): void {
        while (this.subs.length > 0) {
            this.subs.pop().unsubscribe();
        }
    }

    protected langChange(): void {
        this.search();
    }

    protected formatName(entry: any): string {
        let name = '';
        if (entry.provider_name) {
            name += entry.provider_name;
        }
        if (entry.model) {
            if (name.length > 0) {
                name += ', ';
            }
            name += entry.model;
        }
        let part = '';
        if (entry.platform) {
            if (entry.platform === 'RMBTws' && this.rmbtwsTrans != null) {
                part += this.rmbtwsTrans;
            } else {
                part += entry.platform;
            }
        }
        if (entry.network_group_name) {
            if (part.length > 0) {
                part += ' ';
            }
            part += entry.network_group_name;
        }
        if (part != null && part.length > 0) {
            if (name != null && name.length > 0) {
                name += ' ';
            }
            name += '(' + part + ')';
        }
        if (name == null || name.length === 0) {
            name = '-';
        }
        return name;
    }

    protected searchExecute(more: boolean): void {
        if (this.loading) {
            this.logger.warn('Already loading');
            return;
        }
        this.loading = true;
        let params = Object.assign({}, this.queryParams);
        const nfp: NumberFormatPipe = new NumberFormatPipe();
        const sfp: SpeedFormatPipe = new SpeedFormatPipe();
        const pfp: PingFormatPipe = new PingFormatPipe();
        const rp: RoundPipe = new RoundPipe();
        const ffp: FixedFormatPipe = new FixedFormatPipe();
        const places = 2;

        if (this.config.result_list && this.config.result_list.default_search_data) {
            params = Object.assign(params, this.config.result_list.default_search_data);
        }
        if (more && this.cursor) {
            params.cursor = this.cursor;
        }

        this.requests.getJson<any>(this.config.servers.statistic + 'opentests/search', params)
            .subscribe(
            (data: any) => {
                this.logger.debug('List', data);
                if (!more) {
                    this.configService.clearArray(this.results);
                }
                for (const entry of data.results) {

                    this.results.push({
                        time: entry.time,
                        down: ffp.transform(rp.transform(nfp.transform(entry.download_kbit, 0.001), places), places),
                        up: ffp.transform(rp.transform(nfp.transform(entry.upload_kbit, 0.001), places), places),
                        name: this.formatName(entry),
                        signal: entry.signal_strength,
                        ping: ffp.transform(rp.transform(nfp.transform(entry.ping_ms), places), places),
                        uuid: entry.open_test_uuid
                    });
                }
                this.cursor = data.next_cursor;
            },
            (err: HttpErrorResponse) => {
                this.logger.error('searchExecute()', err);
                this.loading = false;
                },
            () => {
                this.loading = false;
            }
        );
    }

    search() {
        this.logger.debug('search()');
        this.searchExecute(false);
    }

    more() {
        this.searchExecute(true);
    }

    openResult(uuid: string) {
        if (!uuid) {
            this.logger.warn('No uuid');
            return false;
        }
        this.router.navigate([this.urlpath, uuid]);
        return false;
    }
}
