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
