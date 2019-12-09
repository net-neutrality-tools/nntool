import { BasicResponseAPI } from '../basic-response.api';
import { SettingsResponseAPI } from '../settings/settings-response.api';

export class RegistrationResponseAPI extends BasicResponseAPI {
  public agent_uuid: string;

  /**
   * @see SettingsResponse
   */
  public settings: SettingsResponseAPI;
}
