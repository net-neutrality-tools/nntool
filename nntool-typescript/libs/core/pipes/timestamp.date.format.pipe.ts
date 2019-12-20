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
import { isValidDate } from './utils';

@Pipe({ name: 'formatDateTimestampToLocal' })
export class TimestampLocalDateFormatPipe implements PipeTransform {
  public transform(value: string | number): string {
    const errorResult = '';
    if (value === undefined || value === null) {
      return errorResult;
    }
    let temp: Date = new Date(value as any);
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
