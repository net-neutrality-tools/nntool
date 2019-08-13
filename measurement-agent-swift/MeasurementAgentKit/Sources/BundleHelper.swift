// MeasurementAgentKit: BundleHelper.swift, created on 11.07.19
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

public class BundleHelper {

    public class func getBundleGitInfoString() -> String? {
        guard let bundleInfo = Bundle.main.infoDictionary else {
            return nil
        }

        //let buildDate    = bundleInfo["BuildDate"]      as? String ?? "n/a"
        let branch       = bundleInfo["GitBranch"]      as? String ?? "n/a"
        let commitCount  = bundleInfo["GitCommitCount"] as? String ?? "n/a"
        let commit       = bundleInfo["GitCommit"]      as? String ?? "n/a"

        return "\(branch)|\(commitCount)|\(commit)"
    }

    public class func getAppVersionInfo() -> (String?, Int?, String?) {
        guard let bundleInfo = Bundle.main.infoDictionary else {
            return (nil, nil, nil)
        }

        let versionName = bundleInfo["CFBundleShortVersionString"] as? String
        var versionCode: Int?

        if let versionCodeString = bundleInfo["CFBundleVersion"] as? String {
            versionCode = Int(versionCodeString)
        }

        let buildDate = bundleInfo["BuildDate"] as? String ?? "n/a"

        return (versionName, versionCode, buildDate)
    }
}
