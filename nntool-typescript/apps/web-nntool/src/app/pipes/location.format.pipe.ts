import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'formatLocation' })
export class LocationFormatPipe implements PipeTransform {
  public transform(value: number, type: 'lat' | 'lng'): string {
    if (value !== 0 && !value) {
      return null;
    }
    let val: string;

    if (type === 'lat') {
      val = 'N ';
    } else {
      val = 'E ';
    }

    const deg: number = Math.floor(value);
    const minutes: number = (value - deg) * 60;
    return val + deg + '° ' + minutes.toFixed(3) + "'";
  }
}
