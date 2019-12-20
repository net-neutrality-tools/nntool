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

export enum GroupTypes {}

export interface GroupedValue {
  key?: string;
  type?: string;
}

export interface FormattedValue extends GroupedValue {
  title: string;
  value: any;
  unit?: string;
  icon?: string;
}

export interface ClassifiedValue extends FormattedValue {
  classification: number;
  color?: string;
}

export interface Grouped {
  title: string;
  icon?: string;
  key?: string;
  type: string;
}

export interface SpeedGraphGroup extends Grouped {
  values: Array<{
    title: string;
    curveType: 'upload' | 'download' | 'signal';
    values: Array<{
      x: number;
      y: number;
    }>;
  }>;
}

export interface ListGrouped extends Grouped {
  values?: Array<FormattedValue | ClassifiedValue | SpeedGraphGroup>;
}

export interface QosGroup extends Grouped {
  description: string;
  tests: any[];
  successes: number;
  failures: number;
  shown: boolean;
}

interface Measurement {
  uuid?: string;
  openUuid?: string;
  time?: number;
  location?: {
    latitude: number;
    longitude: number;
    value?: string;
  };
  locationPath?: Array<{
    latitude: number;
    longitude: number;
    accuracy?: number;
    timeElapsed?: number;
  }>;

  overview?: ListGrouped;
  more?: ListGrouped[];
  details?: Array<ListGrouped | SpeedGraphGroup>;
  qos?: QosGroup[];
  hasQos?: boolean;
}

export default Measurement;
