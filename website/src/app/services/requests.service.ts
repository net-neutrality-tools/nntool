import {Injectable, ElementRef} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

import {Observable} from "rxjs";
import {first} from "rxjs/operators";

import {Logger, LoggerService} from "./log.service";


@Injectable()
export class RequestsService {

    private static logger: Logger = LoggerService.getLogger("RequestsService");

    constructor (private http: HttpClient) {

    }

    private prepareHeaders (
        headers?: {[key: string]: any}, setAcceptJson: boolean = true, setContentTypeJson: boolean = true
    ): HttpHeaders {
        let res: HttpHeaders = new HttpHeaders();
        if (setAcceptJson) {
            res = res.set('Accept', 'application/json');
        }
        if (setContentTypeJson) {
            res = res.set('Content-Type', 'application/json');
        }

        if (headers) {
            for (let key in headers) {
                if (!headers.hasOwnProperty(key)) {
                    continue;
                }
                const val: any = headers[key];
                if (typeof val !== "undefined") {
                    res = res.set(key, val);
                }
            }
        }

        return res;
    }

    private prepareParams (params?: {[key: string]: any}): HttpParams {
        let ps: HttpParams = new HttpParams();
        if (params != null && params) {
            for (let key in params) {
                if (!params.hasOwnProperty(key)) {
                    continue;
                }
                if (params[key] instanceof Array) {
                    for (const it of params[key]) {
                        ps = ps.append(key, it);
                    }
                } else {
                    ps = ps.set(key, params[key]);
                }
            }
        }
        return ps;
    }

    getJson<T> (uri: string, params?: {[key: string]: any}, headers?: {[key: string]: any}): Observable<T> {
        RequestsService.logger.debug('getJson', 'uri', uri, 'params', params);

        return this.http.get<T>(
            uri, {
                headers: this.prepareHeaders(headers, true, false),
                params: this.prepareParams(params),
            }
        )
            .pipe<T>( first() );
    }

    putJson<T> (uri: string, data: any, params?: {[key: string]: any}, headers?: {[key: string]: any}): Observable<T> {
        RequestsService.logger.debug('putJson', 'uri', uri, 'params', params, 'data', data);

        return this.http.put<T>(
            uri,
            JSON.stringify(data),
            {
                headers: this.prepareHeaders(headers),
                params: this.prepareParams(params),
            }
        )
            .pipe<T>( first() );
    }

    postJson<T> (uri: string, data: any, params?: {[key: string]: any}, headers?: {[key: string]: any}): Observable<T> {
        RequestsService.logger.debug('postJson', 'uri', uri, 'params', params, 'data', data);

        return this.http.post<T>(
                uri,
                JSON.stringify(data),
            {
                    headers: this.prepareHeaders(headers),
                    params: this.prepareParams(params),
                }
            )
            .pipe<T>( first() );
    }

    deleteJson<T> (uri: string, params?: {[key: string]: any}, headers?: {[key: string]: any}): Observable<T> {
        RequestsService.logger.debug('deleteJson', 'uri', uri, 'params', params);

        return this.http.delete<T>(
            uri, {
                headers: this.prepareHeaders(headers),
                params: this.prepareParams(params),
            }
        )
            .pipe<T>( first() );
    }

    getScript (src: string, elementRef: ElementRef, onload?: () => void): Observable<any> {
        return Observable.create((observer: any) => {
            let s = document.createElement("script");
            s.type = "text/javascript";
            s.src = src;
            s.onload = () => {
                if (onload) {
                    onload();
                }
                observer.next(true);
                observer.complete();
            };
            elementRef.nativeElement.appendChild(s);
        });
    }

    getCss (src: string): void {
        $('head').append(
            $('<link rel="stylesheet" type="text/css" />').attr("href", src)
        );
    }
}