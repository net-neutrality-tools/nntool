import { Pipe, PipeTransform } from '@angular/core';


@Pipe({name: 'formatDateUTCToLocal'})
export class UTCLocalDateFormatPipe implements PipeTransform {

    transform (value: string): string {
        if (value === undefined || value === null || value === "") {
            return value;
        }
        if (value.indexOf("Z") === -1 && value.indexOf("T") === -1) {
            // No timezone info -> force UTC
            let split: string[] = value.split(" ");
            value = split[0] + "T" + split[1] + "Z";
        }
        let temp: Date = new Date(value);
        return temp.toLocaleString();
    }
}