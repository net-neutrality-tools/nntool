import { SlideableItem } from '../../animation/slideable-item';
import { QoSResult } from './full-measurement-response.api';

export class QoSResultGroupHolder extends SlideableItem {
  public icon: string;
  public title: string;
  public description: string;
  public successes = 0;
  public failures = 0;
  public tests = new Array<QoSResult>();
}
