import { Pipe, PipeTransform } from '@angular/core';


@Pipe({name: 'round'})
export class RoundPipe implements PipeTransform {

    transform(value: number, places: number = 2): number {
        return Math.round(value * 10 ** places) / 10 ** places;
    }
}
