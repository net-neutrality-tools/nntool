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
