import {BasicRequestAPI} from "../basic-request.api";

export class RegistrationRequestAPI extends BasicRequestAPI {

    /**
     * Boolean whether the measurement agent has accepted the presented terms and conditions.
     */
    terms_and_conditions_accepted: boolean;

    /**
     * The version of the presented terms and conditions that the measurement agent agreed to (or declined).
     */
    terms_and_conditions_accepted_version: number;

    /**
     * The measurement agent's group name/identifier.
     */
    group_name: string;
}

