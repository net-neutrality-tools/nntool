/*******************************************************************************
 * Copyright 2017-2019 Benjamin Pucher (alladin-IT GmbH)
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
@IBDesignable class AbstractTwoArcGaugeView: UIView, TwoArcGaugeProtocol {

    ///
    var _progress: Double = 0

    ///
    var currentPhase: GaugePhase = .initialize {
        didSet {
            update()
        }
    }

    ///
    @IBInspectable var arcWidth: CGFloat = 35 {
        didSet {
            update()
        }
    }

    ///
    @IBInspectable var arcSpace: CGFloat = 20 {
        didSet {
            update()
        }
    }

    ///
    @IBInspectable var angle: CGFloat = 240 {
        didSet {
            update()
        }
    }

    ///
    var progress: Double = 0 {
        didSet {
            _progress = progress
            update()
        }
    }

    ///
    var value: Double = 0 {
        didSet {
            update()
        }
    }

    ///
    @IBInspectable var baseColor: UIColor = BEREC_LIGHT_GRAY {
        didSet {
            update()
        }
    }

    ///
    @IBInspectable var progressColor: UIColor = BEREC_DARK_BLUE {
        didSet {
            update()
        }
    }

    ///
    @IBInspectable var valueColor: UIColor = BEREC_RED {
        didSet {
            update()
        }
    }

    ///
    @IBInspectable var textColor: UIColor = BEREC_WHITE {
        didSet {
            update()
        }
    }

    ///
    override init(frame: CGRect) {
        super.init(frame: frame)

        initGauge()
    }

    ///
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        initGauge()
    }

    ///
    func initGauge() {

    }

    ///
    func update() {
        setNeedsDisplay()
    }
    
    func reset() {
        progress = 0
        value = 0
    }

    ///
    override func prepareForInterfaceBuilder() {
        update()
    }

    ///
    func calculateRadius() -> (CGFloat, CGFloat) {
        let outerArcRadius = (bounds.width - 10 - arcWidth) / 2
        let innerArcRadius = outerArcRadius - arcWidth - arcSpace

        return (outerArcRadius, innerArcRadius)
    }
}
