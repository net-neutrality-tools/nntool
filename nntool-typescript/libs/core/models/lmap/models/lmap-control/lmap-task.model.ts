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

import { LmapFunction } from '../shared/lmap-function.model';
import { LmapOption } from '../shared/lmap-option.model';

export class LmapTask {
  /**
   * The unique name of a Task.
   */
  public name: string;

  /**
   * A list of entries in a registry identifying functions.
   */
  public function: LmapFunction[];

  /**
   * The (local) program to invoke in order to execute the Task.
   * If this leaf is not set, then the system will try to identify a suitable program based on the registry information present.
   */
  public program: string;

  /**
   * The list of Task-specific options.
   */
  public option: LmapOption[];

  /**
   * A set of Task-specific tags that are reported together with the measurement results to a Collector.
   * A tag can be used, for example, to carry the Measurement Cycle ID.
   */
  public tag: string[];
}
