import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  templateUrl: './portal-home.component.html'
})
export class PortalHomeComponent {
  constructor(public translateService: TranslateService) {}
}
