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

@Pipe({ name: 'formatClassification' })
export class ClassificationFormatPipe implements PipeTransform {
  public transform(value: number): string {
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
