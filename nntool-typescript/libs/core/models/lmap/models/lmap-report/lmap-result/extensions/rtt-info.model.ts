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

import { Rtt } from './rtt.model';

export class RttInfo {
  /**
   * List of all measured RTTs.
   */
  public rtts: Rtt[];

  /**
   * The number of RTT packets to send, as instructed by the server.
   */
  public requested_num_packets: number;

  /**
   * The actual number of sent RTT packets.
   */
  public num_sent: number;

  /**
   * The actual number of received RTT packets.
   */
  public num_received: number;

  /**
   * The actual number of failed RTT packets.
   */
  public num_error: number;

  /**
   * The actual number of missing RTT packets.
   */

  public num_missing: number;

  /**
   * The actual size of RTT packets.
   */
  public packet_size: number;

  /**
   * Average rtt in nanoseconds
   */
  public average_ns: number;

  /**
   * Maximum rtt in nanoseconds
   */
  public maximum_ns: number;

  /**
   * Median rtt in nanoseconds
   */
  public median_ns: number;

  /**
   * Minimum rtt in nanoseconds
   */
  public minimum_ns: number;

  /**
   * Standard deviation rtt in nanoseconds
   */
  public standard_deviation_ns: number;
}
