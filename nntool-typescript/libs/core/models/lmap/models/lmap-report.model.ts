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

import { LmapResult } from './lmap-report/lmap-result.model';
import { RequestInfoAPI } from '../../../../test/models/api/request-info.api';
import { TimeBasedResultAPI } from '../../../../test/models/measurements/time-based-result.api';

export class LmapReport {
  /**
   * The date and time when this result report was sent to a Collector.
   */
  public date: string; // TODO: change back to Date

  /**
   * The agent-id of the agent from which this report originates.
   */
  public 'agent-id'?: string;

  /**
   * The group-id of the agent from which this report originates.
   */
  public 'group-id'?: string;

  /**
   * The measurement-point of the agent from which this report originates.
   */
  public 'measurement-point'?: string;

  /**
   * The list of Tasks for which results are reported.
   */
  public result: LmapResult[];

  /**
   * Additional information that is sent by client alongside the request. This contains most information from ClientInfo.
   */
  public additional_request_info: RequestInfoAPI;

  /**
   * This module defines a data model for reporting time based results from Measurement Agents.
   */
  public time_based_result: TimeBasedResultAPI;
}
