import {BasicResponseAPI} from "../basic-response.api";

export class Urls {

    /**
     * Base URL of the controller service of the form "[protocol]://[domain]:[port]/[path]".
     * This domain name should have A and AAAA records.
     */
    controller_service: string;

    /**
     * IPv4-only base URL of the controller service.
     * This domain name must only have an A record.
     */
    controller_service_ipv4: string;

    /**
     * IPv6-only base URL of the controller service.
     * This domain name must only have an AAAA record.
     */
    controller_service_ipv6: string;

    /**
     * Base URL of the collector service of the form "[protocol]://[domain]:[port]/[path]".
     * This domain name should have A and AAAA records.
     */
    collector_service: string;

    /**
     * Base URL of the map service of the form "[protocol]://[domain]:[port]/[path]".
     */
    map_service: string;

    /**
     * Base URL of the statistic service of the form "[protocol]://[domain]:[port]/[path]".
     */
    statistic_service: string;

    /**
     * Base URL of the web site of the form "[protocol]://[domain]:[port]/[path]".
     */
    website: string;
}

export enum QoSMeasurementType {
    TCP = 'TCP',
    UDP = 'UDP',
    DNS = 'DNS',
    NON_TRANSPARENT_PROXY = 'NON_TRANSPARENT_PROXY',
    HTTP_PROXY = 'HTTP_PROXY',
    VOIP = 'VOIP',
    TRACEROUTE = 'TRACEROUTE',
    WEBSITE = 'WEBSITE',
    AUDIO = 'AUDIO',
    ECHO_PROTOCOL = 'ECHO_PROTOCOL',

    MKIT_DASH = 'MKIT_DASH',
    MKIT_WEB_CONNECTIVITY = 'MKIT_WEB_CONNECTIVITY'
}

export class TranslatedQoSTypeInfo {
    /**
     * The translated QoS type name.
     */
    name: string;

    /**
     * The translated QoS type description.
     */
    description: string;
}

export class VersionResponse {
    /**
     * Controller service version number.
     */
    controller_service_version: string;

    /**
     * Collector service version number.
     */
    collector_service_version: string;

    /**
     * Result service version number.
     */
    result_service_version: string;

    /**
     * Map service version number.
     */
    map_service_version: string;

    /**
     * Statistic service version number.
     */
    statistic_service_version: string;
}

export interface SettingsResponseAPI extends BasicResponseAPI {

    /**
     * @see Urls
     */
    urls: Urls;

    /**
     * Map of QoS measurement types to translated type information.
     */
    qos_type_info: Map<QoSMeasurementType, TranslatedQoSTypeInfo>;

    /**
     * @see VersionResponse
     */
    version: VersionResponse;
}

