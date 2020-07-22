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

import { TestState } from '../test-state';
import { PortBlockingTestTypeEnum } from './enums/port-blocking-test-type';

export class PortBlockingTestState extends TestState {
  public types: Array<{
    key: PortBlockingTestTypeEnum;
    ports: Array<{
      packets: {
        requested_packets: number,
        lost: number; 
        received: number;
        sent: number 
      }; 
      delay?: { 
        average_ns: number;
        standard_deviation_ns: number 
      };
      delays?: number[];
      number: number;
      uid: string;
    }>;
  }>;
}
