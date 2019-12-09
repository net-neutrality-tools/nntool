import { HostListener, Injectable } from '@angular/core';

@Injectable()
export class DateParseService {

    public parseDateIntoFormat(date: Date): string {
        let hours: string = "" + date.getHours();
        if (hours.length === 1) {
            hours = "0" + hours;
        }
        let minutes: string = "" + date.getMinutes();
        if (minutes.length === 1) {
            minutes = "0" + minutes;
        }
        return date.getDate() + "." + (date.getMonth() + 1) + "." + date.getFullYear() + " " + hours + ":" + minutes;
    }

}
