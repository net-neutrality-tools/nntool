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

import { GeoLocation } from '../api/request-info.api';

export class PointInTimeValueAPI<T> {
  /**
   * The relative time in nanoseconds to the test start.
   */
  public relative_time_ns: number;

  /**
   * The value recorded at this point in time.
   */
  public value: T;

  constructor(value: T, relative_time_ns: number) {
    this.value = value;
    this.relative_time_ns = relative_time_ns;
  }
}
