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
