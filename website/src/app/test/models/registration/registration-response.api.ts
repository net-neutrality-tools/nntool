import {SettingsResponseAPI} from "../settings/settings-response.api";
import {BasicResponseAPI} from "../basic-response.api";

export class RegistrationResponseAPI extends BasicResponseAPI {

    agent_uuid: string;

    /**
     * @see SettingsResponse
     */
    settings: SettingsResponseAPI;
}

