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
