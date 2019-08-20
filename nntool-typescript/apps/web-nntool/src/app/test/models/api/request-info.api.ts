export enum MeasurementAgentType {
    MOBILE = 'MOBILE',
    BROWSER = 'BROWSER',
    DESKTOP = 'DESKTOP'
}

export class GeoLocation {
    /**
     * Time and date the geographic location information was captured (UTC).
     */
    time: string; // TODO change back to Date

    /**
     * Geographic location accuracy.
     */
    accuracy: number;

    /**
     * Geographic location altitude.
     */
    altitude: number;

    /**
     * Movement heading.
     */
    heading: number;

    /**
     * Movement speed.
     */
    speed: number;

    /**
     * Geographic location provider.
     */
    provider: string;

    /**
     * Geographic location latitude.
     */
    latitude: number;

    /**
     * Geographic location longitude.
     */
    longitude: number;

    /**
     * Relative time in nanoseconds (to measurement begin).
     */
    relative_time_ns: number;
}

export class RequestInfoAPI {

    language: string;

    /**
     * The measurement agent's time zone. Is only stored if a measurement is sent to the server.
     */
    timezone: string;

    /**
     * Type of measurement agent. Can be one of 'MOBILE', 'BROWSER', 'DESKTOP', 'CLI'.
     */
    agent_type: MeasurementAgentType;

    /**
     * The agent's UUID.
     * This value is ignored if the resource path already contains the agent's UUID.
     */
    agent_uuid?: string; // TODO: HERAUSFINDEN OB UUID ODER ID
    agent_id?: string; // TODO: HERAUSFINDEN OB UUID ODER ID

    /**
     * Operating system name.
     */
    os_name: string;

    /**
     * Operating system version.
     */
    os_version: string;

    /**
     * API level of operating system or SDK (e.g. Android API level or Swift SDK version).
     */
    api_level: string;

    /**
     * Device code name.
     */
    code_name: string;

    /**
     * Detailed device designation.
     */
    model: string;

    /**
     * Application version name (e.g. 1.0.0).
     */
    app_version_name: string;

    /**
     * Application version code number (e.g. 10).
     */
    app_version_code: number;

    /**
     * Git revision name.
     */
    app_git_revision: string;

    /**
     * The measurement agent device location at the time the request was sent or null if the measurement agent doesn't have location
     * information.
     */
    geo_location: GeoLocation;
}

