import { Pipe, PipeTransform } from '@angular/core';


@Pipe({name: 'formatPing'})
export class PingFormatPipe implements PipeTransform {

    transform(value: number): string {
        return value.toFixed(1);
    }
}
