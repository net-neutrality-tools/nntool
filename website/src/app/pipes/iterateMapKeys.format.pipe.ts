import { Pipe, PipeTransform } from '@angular/core';


@Pipe({name: 'iterateMapKeys'})
export class IterateMapKeysPipe implements PipeTransform {

    transform (value: {}): string[] {
        let res: string[] =  Object.keys(value);
        return res;
    }
}