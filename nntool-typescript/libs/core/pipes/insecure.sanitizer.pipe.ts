/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'insecureSanitizeHtml'
})
export class InsecureSanitizeHtml implements PipeTransform {
  constructor(private _sanitizer: DomSanitizer) {}

  public transform(v: string, key: string): SafeHtml {
    v = v.replace(/(href=")(#.+?")/g, '$1' + key + '$2');
    return this._sanitizer.bypassSecurityTrustHtml(v);
  }
}
