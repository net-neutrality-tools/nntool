export interface StatisticsSettings {
    graphs: {
        list_filters?: {
            // TODO: defaults
        };
        list_providers?: {
            enabled: boolean;

            /**
             * Query parameters added to request
             */
            additional_query_param?: {[key: string]: any};
        };
        list_devices?: {
            enabled: boolean;

            /**
             * Query parameters added to request
             */
            additional_query_param?: {[key: string]: any};
        };
        os_part?: {
            enabled: boolean;

            /**
             * Query parameters added to request
             */
            additional_query_param?: {[key: string]: any};
        };
        technology_part?: {
            enabled: boolean;
            ignore?: string[];

            /**
             * Query parameters added to request
             */
            additional_query_param?: {[key: string]: any};
        };
        measurements_per_provider_speed?: {
            enabled: boolean;

            /**
             * Query parameters added to request
             */
            additional_query_param?: {[key: string]: any};
        };
        measurements_per_provider_over_time_speed?: {
            enabled: boolean;

            /**
             * Query parameters added to request
             */
            additional_query_param?: {[key: string]: any};

            /**
             * Selection method
             */
            select_method: string;

            /**
             * How many to approx. select
             */
            select_count: number;

            /**
             * Min number of providers to show advanced select
             */
            show_box: number;
        };
        measurements_per_provider_over_time_count?: {
            enabled: boolean;

            /**
             * Query parameters added to request
             */
            additional_query_param?: {[key: string]: any};
        };
        measurements_per_technology_over_time?: {
            enabled: boolean;
            ignore?: string[];

            /**
             * Query parameters added to request
             */
            additional_query_param?: {[key: string]: any};
        };
    };
}
