import {MapSettings} from '../map/map.settings.interface';
import {FeatureSettings} from './features.settings.interface';
import {StatisticsSettings} from '../statistics/statistics.settings.interface';
import {ResultSettings} from '../result/result.settings.interface';
import {ServerSettings} from './server.settings.interface';
import {KeySettings} from './key.settings.interface';
import {ResultListSettings} from '../result-list/result_list.settings.interface';
import {OpendataSettings} from '../opendata/opendata.settings.interface';
import {UserSettings} from '../user/user.settings.interface';


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
     * Settings for result list
     */
    result_list: ResultListSettings;

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
     * Settings for result detail
     */
    result: ResultSettings;

    /**
     * Color information
     */
    colors: {
        groups: {
            [key: string]: string[]
        };

        gauge?: {[key: string]: string};
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
}
