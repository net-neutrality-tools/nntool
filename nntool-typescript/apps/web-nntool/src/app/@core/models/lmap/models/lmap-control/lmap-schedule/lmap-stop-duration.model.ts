import { LmapStop } from './lmap-stop.model';

export class LmapStopDuration extends LmapStop {
  /**
   * The duration controlling the graceful forced termination of the scheduled Actions, in ms.
   */
  public duration?: number;
}
