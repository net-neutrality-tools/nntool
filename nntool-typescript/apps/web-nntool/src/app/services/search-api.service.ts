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
export class SearchApiService {
  private config: WebsiteSettings;

  constructor(
    private logger: NGXLogger,
    private requests: RequestsService,
    private configService: ConfigService,
    private http: HttpClient
  ) {
    this.config = this.configService.getConfig();
  }

  getServerDataSource(): SpringServerDataSource {
    return new SpringServerDataSource(
      this.http,
      new SpringServerSourceConf({
        endPoint: this.config.servers.search + 'measurements',
        mapFunction: (dto: any) => dto //this.groupMapper.dtoToModel(dto)
      })
    );
  }

  public getSingleOpenDataMeasurement(openDataUuid: string): Observable<any> {
    return new Observable((observer: any) => {
      this.requests
        .getJson<any>(Location.joinWithSlash(this.config.servers.search, 'measurements/' + openDataUuid), {})
        .subscribe(
          (data: any) => {
            this.logger.debug('opendata measurement: ', data);

            observer.next(data);
          },
          (error: HttpErrorResponse) => {
            this.logger.error('Error retrieving single open-data measurement', error);
            observer.error();
          },
          () => observer.complete()
        );
    });
  }

  public getSingleGroupedOpenDataMeasurement(openDataUuid: string): Observable<any> {
    return new Observable((observer: any) => {
      this.requests
        .getJson<any>(Location.joinWithSlash(this.config.servers.search, 'measurements/' + openDataUuid + '/details'), {})
        .subscribe(
          (data: any) => {
            this.logger.debug('grouped opendata measurement: ', data);

            observer.next(data);
          },
          (error: HttpErrorResponse) => {
            this.logger.error('Error retrieving single grouped open-data measurement', error);
            observer.error();
          },
          () => observer.complete()
        );
    });
  }

  public exportSingleOpenDataMeasurement(openDataUuid: string, extension: string): Observable<any> {
    // Location.joinWithSlash(this.config.servers.search, 'measurements/' + openDataUuid + '.' + extension),
    return new Observable((observer: any) => { });
  }

  ////

  public getOpenDataMeasurements(): Observable<any> {
    return new Observable((observer: any) => {
      this.requests
        .getJson<any>(
          // TODO: use search-service url from settings request
          Location.joinWithSlash(this.config.servers.search, 'measurements'),
          {}
        )
        .subscribe(
          (data: any) => {
            this.logger.debug('opendata measurements', data);

            observer.next(data);
          },
          (err: HttpErrorResponse) => {
            this.logger.error('Error retrieving opendata measurements', err);
            observer.error(err);
          },
          () => {
            observer.complete();
          }
        );
    });
  }
}
