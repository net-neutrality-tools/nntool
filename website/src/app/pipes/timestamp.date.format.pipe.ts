import { Pipe, PipeTransform } from '@angular/core';
import {isValidDate} from './utils';


@Pipe({name: 'formatDateTimestampToLocal'})
export class TimestampLocalDateFormatPipe implements PipeTransform {

    transform(value: string | number): string {
        const errorResult = '';
        if (value === undefined || value === null) {
            return errorResult;
        }
        let temp: Date = new Date((value as any));
        if (isValidDate(temp)) {
            return temp.toLocaleString();
        }
        if (typeof value === 'string') {
            const dateRegex: any = /(\d+)-(\d+)-(\d+) (\d+):(\d+):(\d+)/;
            const match: any = value.match(dateRegex);
            if (match == null) {
                return errorResult;
            }
            if (match.length === 4) {
                temp = new Date(match[1], match[2], match[3]);
            } else if (match.length === 7) {
                temp = new Date(match[1], match[2], match[3], match[4], match[5], match[6]);
            } else {
                return errorResult;
            }
        } else {
            console.warn('Cannot guess time for ', value);
            return errorResult;
        }
        if (isValidDate(temp)) {
            return temp.toLocaleString();
        }
        return errorResult;
    }
}
