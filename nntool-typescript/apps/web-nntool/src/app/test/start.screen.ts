import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-screen-start',
  templateUrl: './start.screen.html'
})
export class StartScreenComponent {
  @Input()
  public _screenNr: number;
  @Output()
  public _screenNrChange = new EventEmitter<number>();
}
