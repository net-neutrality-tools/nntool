// ios-app: SpeedMeasurementBasicResultView.swift, created on 26.03.19
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
@IBDesignable class SpeedMeasurementBasicResultView: NibView {

    @IBOutlet private var rttValueLabel: UILabel?
    @IBOutlet private var downloadValueLabel: UILabel?
    @IBOutlet private var uploadValueLabel: UILabel?
    
    func setText(_ text: String, forPhase phase: SpeedMeasurementPhase) {
        switch phase {
        case .rtt: rttValueLabel?.text = text
        case .download: downloadValueLabel?.text = text
        case .upload: uploadValueLabel?.text = text
        default: break
        }
    }
    
    func reset() {
        rttValueLabel?.text = " " // empty string or nil causes stack view to collapse
        downloadValueLabel?.text = " "
        uploadValueLabel?.text = " "
    }
}
