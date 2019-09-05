import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../services/config.service';
import { RequestsService } from '../services/requests.service';
import { WebsiteSettings } from '../settings/settings.interface';
import { ResultGroupResponse } from '../history/model/result.groups';
import { SearchApiService } from '../services/search-api.service';

@Component({
  templateUrl: './open-data-result.component.html'
})
export class OpenDataResultComponent implements OnInit {
  private configService: ConfigService;
  private requests: RequestsService;
  private config: WebsiteSettings;
  private translationKey: string;
  private urlpath: string;
  private openDataUuid: string;
  private response: ResultGroupResponse;
  private loading: boolean;

  constructor(
    private translationService: TranslateService,
    private activatedRoute: ActivatedRoute,
    private searchApiService: SearchApiService
  ) {
    this.loading = true;
    this.urlpath = '/history/';
    this.translationKey = 'RESULT.DETAIL';
    this.openDataUuid = activatedRoute.snapshot.paramMap.get('openDataUuid');
  }

  public ngOnInit() {
    this.activatedRoute.paramMap.subscribe(paramMap => (this.openDataUuid = paramMap.get('openDataUuid')));

    this.searchApiService.getSingleGroupedOpenDataMeasurement(this.openDataUuid).subscribe((data: any) => {
      this.loading = false;
      this.response = data.data;
    });
  }
}
