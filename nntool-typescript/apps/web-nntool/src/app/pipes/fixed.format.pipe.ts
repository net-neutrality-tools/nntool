import { Pipe, PipeTransform } from '@angular/core';


@Pipe({name: 'formatFixed'})
export class FixedFormatPipe implements PipeTransform {
    transform(value: number, places?: number): string {
        if (places === undefined || places === null) {
            places = 1;
        }
        if (value === undefined || value === null) {
            return '';
        }
        return value.toFixed(places);
    }
}
