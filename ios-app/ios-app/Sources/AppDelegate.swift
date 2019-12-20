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

import UIKit
import MeasurementAgentKit
import GoogleMaps

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        applyAppearance()

        if GOOGLE_MAPS_API_KEY != "" {
            GMSServices.provideAPIKey(GOOGLE_MAPS_API_KEY)
        }

        afterStart(isNewlyLaunched: true)

        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {

    }

    func applicationDidEnterBackground(_ application: UIApplication) {

    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        afterStart(isNewlyLaunched: false)
    }

    func applicationDidBecomeActive(_ application: UIApplication) {

    }

    func applicationWillTerminate(_ application: UIApplication) {

    }

    func applyAppearance() {
        UINavigationBar.appearance().tintColor = APP_TINT_COLOR
        UINavigationBar.appearance().barTintColor = UIColor.white
        UINavigationBar.appearance().titleTextAttributes = [NSAttributedString.Key.foregroundColor: APP_TINT_COLOR]
    }

    ///
    func afterStart(isNewlyLaunched: Bool) {
        if isNewlyLaunched {
            logger.debug(BundleHelper.getAppVersionInfo())
            logger.debug(BundleHelper.getBundleGitInfoString())

            logger.debugExec {
                MeasurementAgentSettingsHelper.debugSavedSettings()
            }
        }

        // Refresh MeasurementAgent settings after App launch
        if MEASUREMENT_AGENT.isRegistered() {
            logger.debug("reloading settings (newlyLaunched: \(isNewlyLaunched))")
            MEASUREMENT_AGENT.updateSettings()
        }
    }
}
