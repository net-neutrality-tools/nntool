import {NumberFormatPipe} from "./number.format.pipe";
import {SpeedFormatPipe} from "./speed.format.pipe";
import {PingFormatPipe} from "./ping.format.pipe";
import {FixedFormatPipe} from "./fixed.format.pipe";
import {UTCLocalDateFormatPipe} from "./utc.date.format.pipe";
import {LocationFormatPipe} from "./location.format.pipe";


export function format (value: any, setting: any): any {
    if (value === null || value === undefined) {
        // No value
        return value;
    }
    if (!setting || (!setting.formats && (typeof setting !== "string"))) {
        // No formatting
        return value;
    }
    if (typeof setting === "string") {
        setting = {
            formats: [{
                format: setting
            }]
        };
    }
    let nf: NumberFormatPipe = new NumberFormatPipe();
    let sf: SpeedFormatPipe = new SpeedFormatPipe();
    let pf: PingFormatPipe = new PingFormatPipe();
    let ff: FixedFormatPipe = new FixedFormatPipe();
    let uldf: UTCLocalDateFormatPipe = new UTCLocalDateFormatPipe();
    let lf: LocationFormatPipe = new LocationFormatPipe();

    for (let formatter of setting.formats) {
        switch (formatter.format) {
            case "number":
                value = nf.transform(value, formatter.factor);
                break;
            case "speed":
                value = sf.transform(value);
                break;
            case "ping":
                value = pf.transform(value);
                break;
            case "accuracy":
                formatter.places = 1;
            case "fixed":
                value = ff.transform(value, formatter.places);
                break;
            case "utc2local":
                value = uldf.transform(value);
                break;
            case "lat":
                value = lf.transform(value, "lat");
                break;
            case "lng":
                value = lf.transform(value, "lng");
                break;
        }
    }

    return value;
}


export function isValidDate (d: Date): boolean {
    return Object.prototype.toString.call(d) === '[object Date]' && !isNaN(d.getTime());
}