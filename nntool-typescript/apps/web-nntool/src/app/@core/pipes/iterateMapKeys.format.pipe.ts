import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'iterateMapKeys' })
export class IterateMapKeysPipe implements PipeTransform {
  public transform(value: {}): string[] {
    const res: string[] = Object.keys(value);
    return res;
  }
}
