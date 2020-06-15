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

import { SelectOption } from '../../filters/select_option.interface';

export interface MapSelectMapOptions extends SelectOption {
  legend: {
    colors: string;
    captions: string[];
  };

  thresholds?: {
    [key: string]: {
      colors: string;
      captions: string[];
    };
  };
}

export interface MapOptions {
  statistical_method?: SelectOption[];
  map_options: MapSelectMapOptions[];
  period?: SelectOption[];
  technology?: SelectOption[];
  operator?: SelectOption[];
  layer_type: SelectOption[];
}

export interface MapSettings {
  view: any;
  showLegend?: boolean;
  options?: MapOptions;
  filter_defaults?: {
    statistical_method?: any;
    map_options?: any;
    period?: any;
    technology?: any;
    operator?: any;
  };
}
