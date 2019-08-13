import { Component } from '@angular/core';

// xplat
import { AppBaseComponent } from '@nntool-typescript/web';

@Component({
  selector: 'nntool-root',
  templateUrl: './app.component.html'
})
export class AppComponent extends AppBaseComponent {
  constructor() {
    super();
  }
}
