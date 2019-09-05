import { LmapEventType } from './lmap-event/lmap-event-type.api';

export class LmapEvent {
  /**
   * The unique name of an event source, used when referencing this event.
   */
  public name: string;

  /**
   * This optional leaf adds a random spread to the computation of the event's trigger time.
   * The random spread is a uniformly distributed random number taken from the interval [0:random-spread].
   */
  public 'random-spread': number;

  /**
   * The optional cycle-interval defines the duration of the time interval in seconds
   *  that is used to calculate cycle numbers.
   *  No cycle number is calculated if the optional cycle-interval does not exist.
   */
  public 'cycle-interval'?: number;

  /**
   * Different types of events are handled by different branches of this choice.
   * Note that this choice can be extended via augmentations.
   */
  public 'event-type'?: LmapEventType;
}
