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

import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { NumberFormatPipe } from './number.format.pipe';
import { SpeedFormatPipe } from './speed.format.pipe';
import { PingFormatPipe } from './ping.format.pipe';
import { LocationFormatPipe } from './location.format.pipe';
import { ClassificationFormatPipe } from './classification.format.pipe';
import { IterateMapKeysPipe } from './iterateMapKeys.format.pipe';
import { FixedFormatPipe } from './fixed.format.pipe';
import { InsecureSanitizeHtml } from './insecure.sanitizer.pipe';
import { UTCLocalDateFormatPipe } from './utc.date.format.pipe';
import { TimestampLocalDateFormatPipe } from './timestamp.date.format.pipe';
import { RoundPipe } from './round.pipe';

const PIPES = [
  NumberFormatPipe,
  SpeedFormatPipe,
  PingFormatPipe,
  LocationFormatPipe,
  ClassificationFormatPipe,
  IterateMapKeysPipe,
  FixedFormatPipe,
  InsecureSanitizeHtml,
  UTCLocalDateFormatPipe,
  TimestampLocalDateFormatPipe,
  RoundPipe
];

@NgModule({
  imports: [SharedModule],
  exports: [...PIPES],
  declarations: [...PIPES]
})
export class PipesModule { }
