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
