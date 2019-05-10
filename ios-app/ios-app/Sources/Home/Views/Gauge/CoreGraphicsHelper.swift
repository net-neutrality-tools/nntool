// ios-app: CoreGraphicsHelper.swift, created on 26.03.19
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
class CoreGraphicsHelper {

    ///
    class func getPointOnCircle(radius: CGFloat, center: CGPoint, phi: CGFloat) -> CGPoint {
        let x = radius * cos(phi) + center.x
        let y = radius * sin(phi) + center.y

        return CGPoint(x: x, y: y)
    }

    ///
    class func deg2rad(_ value: CGFloat) -> CGFloat {
        return value * .pi / 180.0
    }
}
