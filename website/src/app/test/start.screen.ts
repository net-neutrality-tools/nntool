import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-screen-start',
    templateUrl: './start.screen.html'
})
export class StartScreenComponent {
    @Input()
    _screenNr: number;
    @Output()
    _screenNrChange = new EventEmitter<number>();

}
