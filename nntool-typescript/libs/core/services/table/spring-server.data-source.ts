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

// https://github.com/akveo/ng2-smart-table/blob/master/src/ng2-smart-table/lib/data-source/server/server.data-source.ts
// https://github.com/ff-iovcloud-ops/ng2-spring-smart-table/blob/master/src/ng2-smart-table/lib/data-source/server/server.data-source.ts

import { HttpParams, HttpClient } from '@angular/common/http';
import { ServerDataSource } from 'ng2-smart-table';
import { getDeepFromObject } from './helpers';
import { SpringServerSourceConf } from './spring-server-source.conf';
import { Observable } from 'rxjs';

export class SpringServerDataSource extends ServerDataSource {
  protected conf: SpringServerSourceConf;

  protected q: string;
  protected requestedQ: string;

  // change: get custom config object in here
  constructor(protected http: HttpClient, conf: SpringServerSourceConf | {} = {}) {
    super(http, conf);

    this.conf = new SpringServerSourceConf(conf);

    if (!this.conf.endPoint) {
      throw new Error('At least endPoint must be specified as a configuration of the server data source.');
    }
  }

  public setSearchQuery(q: string) {
    this.q = q;
  }

  public getSearchQuery(): string {
    return this.q;
  }

  public removeSearchQuery() {
    this.q = undefined;
  }

  public getPagingConf() {
    let p = this.pagingConf;

    p['page'] -= 1;

    return p;
  }

  public setHttpParam(key: string, additionalParameter: any) {
    this.conf.additonalParameters[key] = additionalParameter;
  }

  // change: add search parameter (q=)
  protected requestElements(): Observable<any> {
    // reset page to 1 if new query is executed
    if (this.q !== this.requestedQ) {
      this.pagingConf['page'] = 1; //every page is reduced by 1 anyway, so to start with the first page, set the page to 1
    }

    let httpParams = this.createRequesParams();

    if (this.q) {
      httpParams = httpParams.set('q', this.q);
    }

    this.requestedQ = this.q;

    return this.http.get(this.conf.endPoint, { params: httpParams, observe: 'response' });
  }

  // change: Spring Pageable does sorting this way: sort=<property>,<direction>
  protected addSortRequestParams (httpParams: HttpParams): HttpParams {
    if (this.sortConf) {
      this.sortConf.forEach(fieldConf => {
        //httpParams = httpParams.set(this.conf.sortFieldKey, fieldConf.field);
        //httpParams = httpParams.set(this.conf.sortDirKey, fieldConf.direction.toUpperCase());
        httpParams = httpParams.set(this.conf.sortFieldKey, fieldConf.field + ',' + fieldConf.direction.toLowerCase());
      });
    }

    return httpParams;
  }

  // change: Spring Pageable is zero-based
  protected addPagerRequestParams (httpParams: HttpParams): HttpParams {
    if (this.pagingConf && this.pagingConf['page'] && this.pagingConf['perPage']) {
      const zeroBasedPage = parseInt(this.pagingConf['page']) - 1;
      httpParams = httpParams.set(this.conf.pagerPageKey, '' + zeroBasedPage);
      httpParams = httpParams.set(this.conf.pagerLimitKey, this.pagingConf['perPage']);
    }

    return httpParams;
  }

  // change: allow additional request parameters
  protected createRequesParams(): HttpParams {
    let httpParams = super.createRequesParams();
    // add additional static params
    if (this.conf.additonalParameters) {
      Object.keys(this.conf.additonalParameters).forEach(key => {
        //If we ever want to send nulls, this will prevent it
        if (this.conf.additonalParameters[key]) {
          httpParams = httpParams.set(key, this.conf.additonalParameters[key]);
        }
      });
    }

    return httpParams;
  }

  /**
   * Extracts array of data from server response
   * @param res
   * @returns {any}
   */
  // change: allow use of custom map function to map DTO's to model objects
  protected extractDataFromResponse(res: any): Array<any> {
    const rawData = res.body;
    const data = !!this.conf.dataKey ? getDeepFromObject(rawData, this.conf.dataKey, []) : rawData;

    if (data instanceof Array) {
      if (this.conf.mapFunction instanceof Function) {
        return data.map(this.conf.mapFunction);
      } else {
        return data;
      }
    }

    throw new Error(`Data must be an array.
    Please check that data extracted from the server response by the key '${this.conf.dataKey}' exists and is array.`);
  }
}
