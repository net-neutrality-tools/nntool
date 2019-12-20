/*******************************************************************************
 * Copyright 2017-2019 alladin-IT GmbH
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
protocol GaugeProtocol {

    ///
    var currentPhase: SpeedMeasurementPhase { get set }

    ///
    var progress: Double { get set }

    ///
    var value: Double { get set }

    ///
    var baseColor: UIColor { get set }

    ///
    var progressColor: UIColor { get set }

    ///
    var valueColor: UIColor { get set }

    ///
    var textColor: UIColor { get set }

    ///
    func initGauge()

    ///
    func update()
}

///
protocol TwoArcGaugeProtocol: GaugeProtocol {

    ///
    var arcWidth: CGFloat { get set }

    ///
    var arcSpace: CGFloat { get set }

    ///
    var angle: CGFloat { get set }
}
