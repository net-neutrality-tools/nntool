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

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { UserService, UserInfo } from '../../core/services/user.service';

@Component({
  templateUrl: './history.deletion.component.html',
  selector: 'history-deletion-component'
})
export class HistoryDeletionComponent implements OnInit {
  protected subs: Subscription[] = [];

  public measurementUuid: string;
  private translationKey: string;

  constructor(
    private logger: NGXLogger,
    private translationService: TranslateService,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private router: Router
  ) {
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
