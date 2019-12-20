/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { ElementRef, Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';

@Injectable()
export class RequestsService {
  constructor(private logger: NGXLogger, private http: HttpClient) {}

  public getJson<T>(uri: string, params?: { [key: string]: any }, headers?: { [key: string]: any }): Observable<T> {
    this.logger.debug('getJson', 'uri', uri, 'params', params);

    return this.http
      .get<T>(uri, {
        headers: this.prepareHeaders(headers, true, false),
        params: this.prepareParams(params)
      })
      .pipe<T>(first());
  }

  public putJson<T>(
    uri: string,
    data: any,
    params?: { [key: string]: any },
    headers?: { [key: string]: any }
  ): Observable<T> {
    this.logger.debug('putJson', 'uri', uri, 'params', params, 'data', data);

    return this.http
      .put<T>(uri, JSON.stringify(data), {
        headers: this.prepareHeaders(headers),
        params: this.prepareParams(params)
      })
      .pipe<T>(first());
  }

  public postJson<T>(
    uri: string,
    data: any,
    params?: { [key: string]: any },
    headers?: { [key: string]: any }
  ): Observable<T> {
    this.logger.debug('postJson', 'uri', uri, 'params', params, 'data', data);
    return this.http
      .post<T>(uri, JSON.stringify(data), {
        headers: this.prepareHeaders(headers),
        params: this.prepareParams(params)
      })
      .pipe<T>(first());
  }

  public deleteJson<T>(uri: string, params?: { [key: string]: any }, headers?: { [key: string]: any }): Observable<T> {
    this.logger.debug('deleteJson', 'uri', uri, 'params', params);

    return this.http
      .delete<T>(uri, {
        headers: this.prepareHeaders(headers),
        params: this.prepareParams(params)
      })
      .pipe<T>(first());
  }

  public getScript(src: string, elementRef: ElementRef, onload?: () => void): Observable<any> {
    return new Observable((observer: any) => {
      const s = document.createElement('script');
      s.type = 'text/javascript';
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

  private prepareHeaders(
    headers?: { [key: string]: any },
    setAcceptJson: boolean = true,
    setContentTypeJson: boolean = true
  ): HttpHeaders {
    let res: HttpHeaders = new HttpHeaders();
    if (setAcceptJson) {
      res = res.set('Accept', 'application/json');
    }
    if (setContentTypeJson) {
      res = res.set('Content-Type', 'application/json');
    }

    if (headers) {
      for (const key in headers) {
        if (!headers.hasOwnProperty(key)) {
          continue;
        }
        const val: any = headers[key];
        if (typeof val !== 'undefined') {
          res = res.set(key, val);
        }
      }
    }

    return res;
  }

  private prepareParams(params?: { [key: string]: any }): HttpParams {
    let ps: HttpParams = new HttpParams();
    if (params != null && params) {
      for (const key in params) {
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

  // getCss(src: string): void {
  // TODO: jquery
  /*$('head').append(
            $('<link rel="stylesheet" type="text/css" />').attr("href", src)
        );*/
  // }
}
