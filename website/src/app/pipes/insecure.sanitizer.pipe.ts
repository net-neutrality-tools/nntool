import {Pipe, PipeTransform} from "@angular/core";
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";


@Pipe({
    name: "insecureSanitizeHtml"
})
export class InsecureSanitizeHtml implements PipeTransform  {

    constructor (private _sanitizer: DomSanitizer) {

    }

    transform (v: string, key: string): SafeHtml {
        v = v.replace(/(href=")(#.+?")/g, "$1" + key + "$2");
        return this._sanitizer.bypassSecurityTrustHtml(v);
    }
}