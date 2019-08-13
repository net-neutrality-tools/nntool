import { Pipe, PipeTransform } from '@angular/core';


@Pipe({name: 'formatClassification'})
export class ClassificationFormatPipe implements PipeTransform {

    transform(value: number): string {
        if (value <= 0 && !value) {
            return null;
        }

        if (value === 3) {
            return 'traffic-good';
        } else if (value === 2) {
            return 'traffic-average';
        } else if (value === 1) {
            return 'traffic-bad';
        }
        return null;
    }
}
