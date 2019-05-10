// ios-app: ProgressInfoBar.swift, created on 26.03.19
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

///
@IBDesignable class ProgressInfoBar: NibView {

    @IBOutlet private var leftIconLabel: UILabel?
    @IBOutlet private var leftValueLabel: UILabel?

    @IBOutlet private var rightIconLabel: UILabel?
    @IBOutlet private var rightValueLabel: UILabel?

    func setLeftValue(value: String, newIcon: IconFont? = nil) {
        if let icon = newIcon {
            leftIconLabel?.icon = icon
        }

        leftValueLabel?.text = value
    }

    func setRightValue(value: String, newIcon: IconFont? = nil) {
        if let icon = newIcon {
            rightIconLabel?.icon = icon
        }

        rightValueLabel?.text = value
    }

    func reset() {
        leftIconLabel?.icon = nil
        leftValueLabel?.text = nil

        rightIconLabel?.icon = nil
        rightValueLabel?.text = nil
    }
}
