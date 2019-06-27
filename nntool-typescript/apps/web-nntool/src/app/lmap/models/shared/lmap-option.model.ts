export abstract class MeasurementTypeParameters {

}

export class LmapOption {

    /**
     * An identifier uniquely identifying an option.
     * This identifier is required by YANG to uniquely identify a name/value pair,
     * but it otherwise has no semantic value.
     */
    id: string;

    /**
     * The name of the option.
     */
    name?: string;

    /**
     * The value of the option.
     */
    value?: string;

    /**
     * The additional measurement parameters of the option.
     */
    'measurement-parameters': MeasurementTypeParameters;

}

