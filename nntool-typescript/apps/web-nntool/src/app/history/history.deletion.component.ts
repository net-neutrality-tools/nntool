import {MeasurementViewComponent} from '../measurement/view.component';
import {Component, OnInit} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LoggerService, Logger } from '../services/log.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService, UserInfo } from '../services/user.service';
import { Subscription } from 'rxjs';

@Component({
    templateUrl: './history.deletion.component.html',
    selector: 'history-deletion-component'
})
export class HistoryDeletionComponent extends MeasurementViewComponent implements OnInit{

    private logger: Logger = LoggerService.getLogger('HistoryDeletionComponent');
    private measurementUuid: string;
    private translationKey: string;
    protected subs: Subscription[] = [];

    constructor(private translationService: TranslateService, private activatedRoute: ActivatedRoute,
                private userService: UserService, private router: Router) {
        super();
        this.translationKey = 'RESULT.DETAIL';
        this.measurementUuid = activatedRoute.snapshot.paramMap.get('uuid');
    }

    ngOnInit() {
        this.activatedRoute.paramMap.subscribe( paramMap => this.measurementUuid = paramMap.get('uuid'));
    }

    disassociate (): boolean {
        if (!this.measurementUuid) {
            return false;
        }
        this.logger.debug("Disassociating", this.measurementUuid);
        const user: UserInfo = this.userService.user;

        if (!user || !user.uuid) {
            return false;
        }

        this.subs.push(this.userService.disassociate(user.uuid, this.measurementUuid).subscribe(
            () => {
                this.logger.debug("Successfully disassociated");
                this.router.navigate(["/history"]);
            },
            (error: any) => {
                this.logger.error("Failed to disassociate", error);
            }
        ));
        return false;
    }

}
