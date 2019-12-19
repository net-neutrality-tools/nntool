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

import { Location } from '@angular/common';
import { HttpErrorResponse, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

import { Observable } from 'rxjs';

import { ConfigService } from './config.service';
import { RequestsService } from './requests.service';
import { SpringServerDataSource } from './table/spring-server.data-source';
import { SpringServerSourceConf } from './table/spring-server-source.conf';
import { WebsiteSettings } from '../models/settings/settings.interface';

@Injectable()
export class ResultApiService {
  private config: WebsiteSettings;

  constructor(
    private logger: NGXLogger,
    private requests: RequestsService,
    private configService: ConfigService,
    private http: HttpClient
  ) {
    this.config = this.configService.getConfig();
  }

  getServerDataSource(agentUuid: string): SpringServerDataSource {
    return new SpringServerDataSource(
      this.http,
      new SpringServerSourceConf({
        endPoint: Location.joinWithSlash(
          this.config.servers.result,
          '/measurement-agents/' + agentUuid + '/measurements'
        ),
        mapFunction: (dto: any) => dto //this.groupMapper.dtoToModel(dto)
      })
    );
  }

  public getSingleMeasurement(uuid: string, agentUuid: string): Observable<any> {
    return new Observable((observer: any) => {
      this.requests
        .getJson<any>(
          Location.joinWithSlash(
            this.config.servers.result,
            '/measurement-agents/' + agentUuid + '/measurements/' + uuid
          ),
          {}
        )
        .subscribe(
          (data: any) => {
            this.logger.debug('measurement: ', data);

            observer.next(data);
          },
          (error: HttpErrorResponse) => {
            this.logger.error('Error retrieving single measurement', error);
            observer.error();
          },
          () => observer.complete()
        );
    });
  }
}
