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

import { FixedFormatPipe } from './fixed.format.pipe';
import { LocationFormatPipe } from './location.format.pipe';
import { NumberFormatPipe } from './number.format.pipe';
import { PingFormatPipe } from './ping.format.pipe';
import { SpeedFormatPipe } from './speed.format.pipe';
import { UTCLocalDateFormatPipe } from './utc.date.format.pipe';

export function format(value: any, setting: any): any {
  if (value === null || value === undefined) {
    // No value
    return value;
  }
  if (!setting || (!setting.formats && typeof setting !== 'string')) {
    // No formatting
    return value;
  }
  if (typeof setting === 'string') {
    setting = {
      formats: [
        {
          format: setting
        }
      ]
    };
  }
  const nf: NumberFormatPipe = new NumberFormatPipe();
  const sf: SpeedFormatPipe = new SpeedFormatPipe();
  const pf: PingFormatPipe = new PingFormatPipe();
  const ff: FixedFormatPipe = new FixedFormatPipe();
  const uldf: UTCLocalDateFormatPipe = new UTCLocalDateFormatPipe();
  const lf: LocationFormatPipe = new LocationFormatPipe();

  for (const formatter of setting.formats) {
    switch (formatter.format) {
      case 'number':
        value = nf.transform(value, formatter.factor);
        break;
      case 'speed':
        value = sf.transform(value);
        break;
      case 'ping':
        value = pf.transform(value);
        break;
      case 'accuracy':
        formatter.places = 1; // TODO: is this the correct behaviour?
      /* falls through */ case 'fixed':
        value = ff.transform(value, formatter.places);
        break;
      case 'utc2local':
        value = uldf.transform(value);
        break;
      case 'lat':
        value = lf.transform(value, 'lat');
        break;
      case 'lng':
        value = lf.transform(value, 'lng');
        break;
    }
  }

  return value;
}

export function isValidDate(d: Date): boolean {
  return Object.prototype.toString.call(d) === '[object Date]' && !isNaN(d.getTime());
}
