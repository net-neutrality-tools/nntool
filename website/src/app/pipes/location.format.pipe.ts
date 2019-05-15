import { Pipe, PipeTransform } from '@angular/core';


@Pipe({name: 'formatLocation'})
export class LocationFormatPipe implements PipeTransform {

    transform (value: number, type: "lat" | "lng"): string {
        if (value !== 0 && !value) {
            return null;
        }
        let val: string;

        if (type === "lat") {
            val = "N ";
        } else {
            val = "E ";
        }

        let deg: number = Math.floor(value);
        let minutes:number = (value - deg) * 60;
        return val + deg + "° " + minutes.toFixed(3) + "'";
    }
}