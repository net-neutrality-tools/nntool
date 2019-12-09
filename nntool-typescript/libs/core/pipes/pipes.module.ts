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
