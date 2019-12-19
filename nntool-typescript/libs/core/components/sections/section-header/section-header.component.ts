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

import { Component, Input } from '@angular/core';

@Component({
  selector: 'nntool-section-header',
  template: `
    <header class="section-header">
      <h1 *ngIf="headerTranslationKey" [innerHTML]="headerTranslationKey | translate"></h1>
      <ng-template [ngIf]="loadingIndicator">
        <figure *ngIf="loading">
          <img src="assets/img/watch-anim.svg" alt="loading content" class="load-animation" />
          <figcaption>{{ 'LOADING' | translate }}</figcaption>
        </figure>
      </ng-template> <!-- TODO: else? -->
      <div [class]="'icon ' + icon" *ngIf="!loading"></div>
      <ng-content></ng-content>
    </header>
  `
})
export class SectionHeaderComponent {
  @Input() headerTranslationKey: string;
  @Input() loadingIndicator = false;

  @Input() icon: string;
  @Input() loading = false;
}
