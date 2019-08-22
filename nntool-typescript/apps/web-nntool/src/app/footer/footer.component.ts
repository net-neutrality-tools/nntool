import { Component } from '@angular/core';
import { Logger, LoggerService } from '../services/log.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html'
})
export class FooterComponent {
  private logger: Logger = LoggerService.getLogger('FooterComponent');
}
