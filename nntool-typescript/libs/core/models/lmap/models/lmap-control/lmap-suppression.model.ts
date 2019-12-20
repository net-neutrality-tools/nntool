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

import { LmapSuppressionState } from './lmap-suppressions/lmap-suppression-state.enum';

export class LmapSuppression {
  /**
   * The locally unique, administratively assigned name for this Suppression.
   */
  public name: string;

  /**
   * The event source controlling the start of the Suppression period.
   * Referencing the {@link LmapEventDto#getName()} of an Action.
   */
  public start?: number;

  /**
   * The event source controlling the end of the Suppression period.
   * If not present, Suppression continues indefinitely.
   * Referencing the {@link LmapEventDto#getName()} of an Action.
   */
  public end: number;

  /**
   * A set of Suppression match patterns.
   * The Suppression will apply to all Schedules (and their Actions)
   * that have a matching value in their suppression-tags
   * and to all Actions that have a matching value in their suppression-tags.
   */
  public match: string[];

  /**
   * If 'stop-running' is true, running Schedules and Actions
   * matching the Suppression will be terminated when Suppression is activated.
   * If 'stop-running' is false, running Schedules and Actions will not be affected if Suppression is activated.
   */
  public 'stop-running'?: boolean;

  /**
   * The current state of the Suppression.
   * Possible values are: enabled, disabled, active.
   */
  public state: LmapSuppressionState;
}
