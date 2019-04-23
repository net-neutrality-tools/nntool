// ios-app: SpeedMeasurementGaugeView.swift, created on 26.03.19
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
@IBDesignable class SpeedMeasurementGaugeView: NibView {

    @IBOutlet private var startButton: UIButton?
    @IBOutlet private var speedMeasurementGauge: SpeedMeasurementGauge?

    @IBOutlet private var networkTypeLabel: UILabel?
    @IBOutlet private var networkDetailLabel: UILabel?

    var startButtonActionCallback: (() -> Void)?

    private static let iconFont = UIFont(name: "berec-icons", size: 50)
    
    private var startButtonSystemFont: UIFont?
    
    override func awakeFromNib() {
        startButtonSystemFont = startButton?.titleLabel?.font
    }
    
    ///
    @IBAction func startButtonPrimaryActionTriggered() {
        startButtonActionCallback?()
        
        //setActivePhase(phase: .initialize)
    }
    
    private func updateButton(icon: IconFont, color: UIColor) {
        startButton?.titleLabel?.font = SpeedMeasurementGaugeView.iconFont
        
        startButton?.setIcon(icon, for: .normal)
        startButton?.backgroundColor = color
    }
    
    func setActivePhase(phase: SpeedMeasurementPhase) {
        updateButton(icon: phase.icon, color: BEREC_DARK_GRAY)
    }
    
    func reset() {
        startButton?.titleLabel?.font = startButtonSystemFont
        
        startButton?.setTitle("GO", for: .normal)
        startButton?.backgroundColor = BEREC_RED
        
        speedMeasurementGauge?.reset()
    }
}
