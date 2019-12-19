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

import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscriber } from 'rxjs';
import { first } from 'rxjs/operators';

import { NGXLogger } from 'ngx-logger';
import { RequestsService } from '../../core/services/requests.service';

@Injectable()
export class ADocService {
  constructor(
    private logger: NGXLogger,
    private requests: RequestsService,
    private translate: TranslateService,
    private http: HttpClient
  ) {}

  public getAdoc(adoc: string, lang: string): Observable<string> {
    // TODO: add fallback to english
    return this.http.get('./assets/i18n/view/' + lang + '/' + adoc + '.html', { responseType: 'text' }).pipe(first());
  }

  public setPage(adoc: string, lang?: string): Observable<string> {
    const transKey: string = 'STATIC.' + adoc.toUpperCase();
    const defLang: string = this.translate.getDefaultLang();

    if (!lang) {
      lang = this.translate.currentLang;
    }

    return new Observable((subscriber: Subscriber<string>) => {
      this.translate.getTranslation(lang).subscribe(
        (data: any) => {
          //this.logger.debug(data);
          if (
            !data ||
            !data.STATIC ||
            !data.STATIC.hasOwnProperty(adoc.toUpperCase()) ||
            !data.hasOwnProperty(transKey)
          ) {
            this.logger.debug('adoc not found', adoc);
            this.getAdoc(adoc, lang).subscribe(
              (data2: any) => {
                this.logger.debug('Loaded ' + adoc + ' in ' + lang);
                this.translate.set(transKey, data2, lang);
                subscriber.next(data2);
                subscriber.complete();
              },
              (err: HttpErrorResponse) => {
                this.logger.debug('Adoc error', lang, err);
                if (err.status === 404 && lang !== defLang) {
                  this.logger.info('Using default lang..');
                  this.getAdoc(adoc, defLang).subscribe(
                    (data2: any) => {
                      this.logger.debug('Loaded ' + adoc + ' in ' + defLang);
                      this.translate.set(transKey, data2, lang);
                      subscriber.next(data2);
                      subscriber.complete();
                    },
                    (err2: HttpErrorResponse) => {
                      this.logger.error('Adoc error', lang, err2);
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
