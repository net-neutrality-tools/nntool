/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { MapSettings } from './map.settings.interface';
import { FeatureSettings } from './features.settings.interface';
import { KeySettings } from './key.settings.interface';
import { ServerSettings } from './server.settings.interface';
import { StatisticsSettings } from './statistics.settings.interface';
import { OpendataSettings } from './opendata.settings.interface';
import { UserSettings } from './user.settings.interface';
import { SocialMediaSettings } from './social-media.settings.interface';

export interface WebsiteSettings {
  /**
   * Enabled features
   */
  features: FeatureSettings;

  /**
   * Server uri settings
   */
  servers: ServerSettings;

  /**
   * Key settings
   */
  keys?: KeySettings;

  /**
   * Settings for map
   */
  map?: MapSettings;

  /**
   * Settings for statistics
   */
  statistics?: StatisticsSettings;

  /**
   * Settings for opendata
   */
  opendata?: OpendataSettings;

  /**
   * Supported languages
   *
   * default language: first in list
   */
  languages: string[];

  /**
   * Which page to use as start/landing page
   */
  landing_page: string;

  /**
   * Color information
   */
  colors: {
    groups: {
      [key: string]: string[];
    };

    gauge?: { [key: string]: string };
  };

  /**
   * Nettest settings
   */
  // TODO: migrate other settings
  nettest?: {
    custom_tc?: boolean;
    tests?: {
      qos?: boolean;
      ndt?: boolean;
      rmbt?: boolean;
    };
    tag?: string;
  };

  /**
   * Nettest User Agent
   */
  user_agent: string;

  /**
   * Settings for user settings
   */
  user: UserSettings;

  /**
   * Which style element to use for classification colors
   */
  classificationColorStyle?: string;

  /**
   * social media (share buttons/links) settings
   */
  socialMediaSettings?: SocialMediaSettings;
}
