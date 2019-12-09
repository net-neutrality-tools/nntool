export enum MeasurementAgentType {
  MOBILE = 'MOBILE',
  BROWSER = 'BROWSER',
  DESKTOP = 'DESKTOP'
}

export class GeoLocation {
  /**
   * Time and date the geographic location information was captured (UTC).
   */
  public time: string; // TODO change back to Date

  /**
   * Geographic location accuracy.
   */
  public accuracy: number;

  /**
   * Geographic location altitude.
   */
  public altitude: number;

  /**
   * Movement heading.
   */
  public heading: number;

  /**
   * Movement speed.
   */
  public speed: number;

  /**
   * Geographic location provider.
   */
  public provider: string;

  /**
   * Geographic location latitude.
   */
  public latitude: number;

  /**
   * Geographic location longitude.
   */
  public longitude: number;

  /**
   * Relative time in nanoseconds (to measurement begin).
   */
  public relative_time_ns: number;
}

export class RequestInfoAPI {
  public language: string;

  /**
   * The measurement agent's time zone. Is only stored if a measurement is sent to the server.
   */
  public timezone: string;

  /**
   * Type of measurement agent. Can be one of 'MOBILE', 'BROWSER', 'DESKTOP', 'CLI'.
   */
  public agent_type: MeasurementAgentType;

  /**
   * The agent's UUID.
   * This value is ignored if the resource path already contains the agent's UUID.
   */
  public agent_uuid?: string; // TODO: HERAUSFINDEN OB UUID ODER ID
  public agent_id?: string; // TODO: HERAUSFINDEN OB UUID ODER ID

  /**
   * Operating system name.
   */
  public os_name: string;

  /**
   * Operating system version.
   */
  public os_version: string;

  /**
   * API level of operating system or SDK (e.g. Android API level or Swift SDK version).
   */
  public api_level: string;

  /**
   * Device code name.
   */
  public code_name: string;

  /**
   * Detailed device designation.
   */
  public model: string;

  /**
   * Application version name (e.g. 1.0.0).
   */
  public app_version_name: string;

  /**
   * Application version code number (e.g. 10).
   */
  public app_version_code: number;

  /**
   * Git revision name.
   */
  public app_git_revision: string;

  /**
   * The measurement agent device location at the time the request was sent or null if the measurement agent doesn't have location
   * information.
   */
  public geo_location: GeoLocation;
}
