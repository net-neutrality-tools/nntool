import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

import { ConfigService } from './config.service';
import { SpringServerDataSource } from './table/spring-server.data-source';
import { SpringServerSourceConf } from './table/spring-server-source.conf';
import { WebsiteSettings } from '../models/settings/settings.interface';
import { Observable } from 'rxjs';

@Injectable()
export class StatisticApiService {
  private config: WebsiteSettings;

  constructor(private logger: NGXLogger, private configService: ConfigService, private http: HttpClient) {
    this.config = this.configService.getConfig();
  }

  public getProviderStatisticsServerDataSource(): SpringServerDataSource {
    return new SpringServerDataSource(
      this.http,
      new SpringServerSourceConf({
        endPoint: this.config.servers.statistic + 'statistics/providers', // TODO: filters
        mapFunction: (dto: any) => dto // this.groupMapper.dtoToModel(dto)
      })
    );
  }

  public getProviderFilterConfiguration(): Observable<any> {
    return this.http.get(this.config.servers.statistic + 'statistics/providers/filters').pipe();
  }
}
