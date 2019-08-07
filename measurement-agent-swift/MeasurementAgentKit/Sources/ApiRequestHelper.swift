// MeasurementAgentKit: ApiRequestHelper.swift, created on 11.07.19
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

class ApiRequestHelper {

    class func buildApiRequestInfo(agentUuid: String? = nil, geoLocation: GeoLocationDto? = nil) -> ApiRequestInfo {
        let requestInfo = ApiRequestInfo()

        requestInfo.agentId = agentUuid
        requestInfo.agentType = .mobile
        requestInfo.apiLevel = "Swift 5"

        let (versionName, versionCode, _) = BundleHelper.getAppVersionInfo()

        requestInfo.appVersionName = versionName
        requestInfo.appVersionCode = versionCode

        requestInfo.appGitRevision = BundleHelper.getBundleGitInfoString()

        requestInfo.codeName = UIDevice.current.model
        requestInfo.geoLocation = geoLocation
        requestInfo.language = {
            let preferredLanguages = Locale.preferredLanguages

            if preferredLanguages.count < 1 {
                return "en"
            }

            let sep = preferredLanguages[0].components(separatedBy: "-")
            return sep[0]
        }()
        requestInfo.model = UIDevice.current.model
        requestInfo.osName = UIDevice.current.systemName
        requestInfo.osVersion = UIDevice.current.systemVersion
        requestInfo.timezone = TimeZone.current.identifier

        return requestInfo
    }
}
