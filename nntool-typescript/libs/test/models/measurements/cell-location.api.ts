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

export class CellLocationAPI {
  /**
   * Contains the cell-ID, if available.
   */
  public cell_id: number;

  /**
   * Contains the area code (e.g. location area code (GSM), tracking area code (LTE)), if available.
   */
  public area_code: number;

  /**
   * Time and date the cell location information was captured (UTC).
   */
  public time: string; // TODO: change back to Date

  /**
   * Contains the primary scrambling code, if available.
   */
  public primary_scrambling_code: number;

  /**
   * Contains the ARFCN (Absolute Radio Frequency Channel Number) (e.g. 16-bit GSM ARFCN or 18-bit LTE EARFCN), if available.
   */
  public arfcn: number;

  /**
   * Relative time in nanoseconds (to measurement begin).
   */
  public relative_time_ns: number;

  /**
   * Geographic location latitude of this cell.
   */
  public latitude: number;

  /**
   * Geographic location longitude of this cell.
   */
  public longitude: number;
}
