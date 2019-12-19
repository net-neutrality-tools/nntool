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

export class ProviderFilterResponseWrapper {
    public data: ProviderFilterResponse;
}

export class ProviderFilterResponse {
    public filters: BasicFilter[];
}

export class BasicFilter {

    public key: string;

    public default_value?: any;

    public query_string?: string;

    public value_multiplier?: number;

    public filter_type: FilterType | string;

    public options?: FilterOption[];
}

export class FilterOption {
    public label: string;

    public value: any;
}

export enum FilterType {
    DROPDOWN,
    INPUT_TEXT,
    INPUT_NUMBER,
    INPUT_DATE
}
