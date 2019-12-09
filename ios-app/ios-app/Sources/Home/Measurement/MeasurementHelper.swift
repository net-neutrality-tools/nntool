// ios-app: MeasurementHelper.swift, created on 25.11.19
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
import UIKit

class MeasurementHelper {

    private init() {

    }

    class func createNoMeasurementTaskEnabledWarningAlert(_ openSettingsCallback: (() -> Void)? = nil) -> UIAlertController {
        let alert = UIAlertController(
            title: R.string.localizable.homeNoMeasurementTaskEnabledAlertTitle(),
            message: R.string.localizable.homeNoMeasurementTaskEnabledAlertMessage(),
            preferredStyle: .alert
        )

        alert.addAction(UIAlertAction(title: R.string.localizable.homeNoMeasurementTaskEnabledAlertAbort(), style: .destructive))

        alert.addAction(UIAlertAction(title: R.string.localizable.homeNoMeasurementTaskEnabledAlertOpenSettings(), style: .default) { _ in
            openSettingsCallback?()
        })

        return alert
    }

    class func createPreMeasurementWarningAlert(_ continueMeasurementCallback: (() -> Void)? = nil) -> UIAlertController {
        let alert = UIAlertController(
            title: R.string.localizable.homePreMeasurementAlertTitle(),
            message: R.string.localizable.homePreMeasurementAlertMessage(),
            preferredStyle: .alert
        )

        alert.addAction(UIAlertAction(title: R.string.localizable.homePreMeasurementAlertAbort(), style: .default))

        alert.addAction(UIAlertAction(title: R.string.localizable.homePreMeasurementAlertContinue(), style: .destructive) { _ in
            continueMeasurementCallback?()
        })

        return alert
    }
}
