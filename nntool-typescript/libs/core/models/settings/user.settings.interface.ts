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

export interface UserSettings {
  allow_query_uuid?: boolean;
  allow_set_uuid?: boolean;
  shown: {
    force_ip4: boolean;
    invisible: boolean;
    anonymous_mode: boolean;
    no_anonymize_before_delete_user?: boolean;
    delete_user: boolean;
    client_uuid?: boolean;
    measurement_selection: boolean;
    measurement_selection_speed: boolean;
    measurement_selection_qos: boolean;
  };
}
