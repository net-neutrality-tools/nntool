import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";

import {Observable, Subscriber} from "rxjs";
import {first} from "rxjs/operators";
import {TranslateService} from "@ngx-translate/core";

import {Logger, LoggerService} from "../services/log.service";
import {RequestsService} from "../services/requests.service";


@Injectable()
export class ADocService {

    private logger: Logger = LoggerService.getLogger("ADocService");


    constructor(
        private requests: RequestsService, private translate: TranslateService, private http: HttpClient
    ) {}

    getAdoc (adoc: string, lang: string): Observable<string> {
        return this.http.get(
            "/i18n/view/" + lang + "/" + adoc + ".html", {responseType: 'text'}
        )
            .pipe( first() );
    }

    setPage (adoc: string, lang?: string): Observable<string> {
        const transKey: string = 'STATIC.' + adoc.toUpperCase();
        const defLang: string = this.translate.getDefaultLang();

        if (!lang) {
            lang = this.translate.currentLang;
        }

        return Observable.create((subscriber: Subscriber<string>) => {
            this.translate.getTranslation(lang).subscribe(
                (data: any) => {
                    this.logger.debug(data);
                    if (
                        !data ||
                        !data.STATIC || !data.STATIC.hasOwnProperty(adoc.toUpperCase()) ||
                        !data.hasOwnProperty(transKey)
                    ) {
                        this.logger.debug('adoc not found', adoc);
                        this.getAdoc(adoc, lang).subscribe(
                            (data: any) => {
                                this.logger.debug("Loaded " + adoc + " in " + lang);
                                this.translate.set(transKey, data, lang);
                                subscriber.next(data);
                                subscriber.complete();
                            },
                            (err: HttpErrorResponse) => {
                                this.logger.debug('Adoc error', lang, err);
                                if (err.status === 404 && lang !== defLang) {
                                    this.logger.info("Using default lang..");
                                    this.getAdoc(adoc, defLang).subscribe(
                                        (data: any) => {
                                            this.logger.debug("Loaded " + adoc + " in " + defLang);
                                            this.translate.set(transKey, data, lang);
                                            subscriber.next(data);
                                            subscriber.complete();
                                        },
                                        (err: HttpErrorResponse) => {
                                            this.logger.error('Adoc error', lang, err);
                                            subscriber.error('Default lang failed');
                                        }
                                    );
                                } else {
                                    subscriber.error('Adoc failed');
                                }
                            }
                        );
                    } else {
                        subscriber.error('Trans empty');
                    }
                },
                (err: any) => {
                    subscriber.error(err);
                }
            );
        });
    }
}