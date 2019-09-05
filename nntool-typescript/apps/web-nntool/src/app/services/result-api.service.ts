import { Location } from '@angular/common';
import { HttpErrorResponse, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

import { Observable } from 'rxjs';

import { ConfigService } from './config.service';
import { RequestsService } from './requests.service';
import { SpringServerDataSource } from './table/spring-server.data-source';
import { SpringServerSourceConf } from './table/spring-server-source.conf';
import { WebsiteSettings } from '../@core/models/settings/settings.interface';

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
