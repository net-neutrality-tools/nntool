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

export class MeasurementTypeParameters {
  public measurement_configuration?: any;

  public download: any;
  public upload: any;
}

export class LmapOption {
  /**
   * An identifier uniquely identifying an option.
   * This identifier is required by YANG to uniquely identify a name/value pair,
   * but it otherwise has no semantic value.
   */
  public id: string;

  /**
   * The name of the option.
   */
  public name?: string;

  /**
   * The value of the option.
   */
  public value?: string;

  /**
   * The additional measurement parameters of the option.
   */
  public 'measurement-parameters': MeasurementTypeParameters;
}
