import { Component, Input } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'nntool-standard-page',
  templateUrl: './standard-page.component.html',
  styleUrls: ['./standard-page.component.less']
})
export class StandardPageComponent {
  private loading = false;

  @Input() titleTranslationKey: string;
  @Input() icon: string;

  constructor(private logger: NGXLogger, private translateService: TranslateService) {}
}
