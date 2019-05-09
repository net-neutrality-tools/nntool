import {Component, EventEmitter, Input, Output} from "@angular/core";


@Component({
    templateUrl: "./app/test/start.screen.html",
    selector: ".screen-start"
})
export class StartScreen {
    @Input()
    _screenNr: number;
    @Output()
    _screenNrChange = new EventEmitter<number>();

}