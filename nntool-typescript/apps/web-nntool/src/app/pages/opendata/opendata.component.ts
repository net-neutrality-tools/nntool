import { Component, OnInit } from '@angular/core';
import { SearchApiService } from '../../@core/services/search-api.service';
import { NGXLogger } from 'ngx-logger';
import { ConfigService } from '../../@core/services/config.service';

@Component({
  templateUrl: './opendata.component.html'
})
export class OpendataComponent implements OnInit {
  private dates: Date[] = [];

  private selectedDate?: Date;

  constructor(
    private logger: NGXLogger,
    private searchApiService: SearchApiService,
    private configService: ConfigService
  ) {}

  public ngOnInit(): void {
    const config = this.configService.getConfig();
    const startDate = config.opendata.startDate;

    let start = new Date(startDate.year, startDate.month - 1);
    const end = new Date();

    while (start <= end) {
      this.dates.push(start);

      start = new Date(start.getTime());
      start.setMonth(start.getMonth() + 1);
    }
  }

  public downloadOpendata(extension: string) {
    if (this.selectedDate === undefined) {
      return;
    }

    this.searchApiService.exportOpenDataMeasurementsByDate(this.selectedDate, extension);
  }
}
