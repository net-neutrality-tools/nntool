import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

declare var window;

@Component({
  templateUrl: './home.component.html'
})
export class HomeComponent {
  public isElectron = typeof window !== 'undefined' && window.process && window.process.type;

  constructor(public translateService: TranslateService) {}
}
