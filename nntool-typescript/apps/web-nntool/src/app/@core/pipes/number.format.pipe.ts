import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'formatNumber' })
export class NumberFormatPipe implements PipeTransform {
  public transform(value: number, factor?: number): number {
    if (factor === undefined || factor === null) {
      factor = 1;
    }
    return value * factor;
  }
}
