import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { MeasurementViewComponent } from '../measurement/view.component';
import { Logger, LoggerService } from '../services/log.service';
import { UserInfo, UserService } from '../services/user.service';

@Component({
  templateUrl: './history.deletion.component.html',
  selector: 'history-deletion-component'
})
export class HistoryDeletionComponent extends MeasurementViewComponent implements OnInit {
  protected subs: Subscription[] = [];

  private logger: Logger = LoggerService.getLogger('HistoryDeletionComponent');
  private measurementUuid: string;
  private translationKey: string;

  constructor(
    private translationService: TranslateService,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private router: Router
  ) {
    super();
    this.translationKey = 'RESULT.DETAIL';
    this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
  }

  public ngOnInit() {
    this.activatedRoute.paramMap.subscribe(paramMap => (this.measurementUuid = paramMap.get('uuid')));
  }

  public disassociate(): boolean {
    if (!this.measurementUuid) {
      return false;
    }
    this.logger.debug('Disassociating', this.measurementUuid);
    const user: UserInfo = this.userService.user;

    if (!user || !user.uuid) {
      return false;
    }

    this.subs.push(
      this.userService.disassociate(user.uuid, this.measurementUuid).subscribe(
        () => {
          this.logger.debug('Successfully disassociated');
          this.router.navigate(['/history']);
        },
        (error: any) => {
          this.logger.error('Failed to disassociate', error);
        }
      )
    );
    return false;
  }
}
