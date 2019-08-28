// MeasurementAgentKit: AgentSettingsHelper.swift, created on 19.08.19
/*******************************************************************************
 * Copyright 2019 Benjamin Pucher (alladin-IT GmbH)
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

import Foundation

enum MeasurementAgentSettingsError: Error {
    case settingsNotFound
    case invalidSettings
}

extension MeasurementAgentSettingsError: LocalizedError {

    var errorDescription: String? {
        switch self {
        case .settingsNotFound: return "Settings not found."
        case .invalidSettings: return "Settings could not be parsed."
        }
    }
}

///
public class MeasurementAgentSettingsHelper {

    // TODO: base64 encode controller url?

    private static let settingsPrefix = "agent_settings_"

    public class func debugSavedSettings() {
        let dict = UserDefaults.standard.dictionaryRepresentation()
        let allSettings = dict
            .filter { $0.key.starts(with: settingsPrefix) }
            .map { (item: (key: String, value: Any?)) -> (String, LocalSettings?) in
                let url = item.key.replacingOccurrences(of: settingsPrefix, with: "")

                guard let data = item.value as? Data else {
                    return (url, nil)
                }

                return (url, try? PropertyListDecoder().decode(LocalSettings.self, from: data))
            }

        logger.debug(allSettings)
    }

    class func getLocalSettings(controllerServiceBaseUrl: String) throws -> LocalSettings {
        guard let data = UserDefaults.standard.data(forKey: settingsPrefix + controllerServiceBaseUrl) else {
            throw MeasurementAgentSettingsError.settingsNotFound
        }

        do {
            return try PropertyListDecoder().decode(LocalSettings.self, from: data)
        } catch {
            throw MeasurementAgentSettingsError.invalidSettings
        }
    }

    class func saveLocalSettings(_ settings: LocalSettings) throws {
        let data: Data

        do {
            data = try PropertyListEncoder().encode(settings)
        } catch {
            throw MeasurementAgentSettingsError.invalidSettings
        }

        UserDefaults.standard.set(data, forKey: settingsPrefix + settings.configuredControllerServiceBaseUrl)
    }
}
