import { Component } from '@angular/core';

declare var window;

@Component({
  templateUrl: './home.component.html'
})
export class HomeComponent {
  public isElectron = typeof window !== 'undefined' && window.process && window.process.type;
}
