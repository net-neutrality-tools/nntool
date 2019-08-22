import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'formatSpeed' })
export class SpeedFormatPipe implements PipeTransform {
  public transform(value: number): string {
    /*
        if (value >= 10) {
            return value.toFixed(0);
        } else if (value >= 1) {
            return value.toFixed(1);
        } else if (value >= 0.1) {
            return value.toFixed(2);
        } else {
            return value.toFixed(3);
        }*/
    if (!value) {
      return '' + value;
    }
    return '' + Math.round(value * 100) / 100;
  }
}
