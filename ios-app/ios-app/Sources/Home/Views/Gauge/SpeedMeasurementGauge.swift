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
@IBDesignable class SpeedMeasurementGauge: AbstractTwoArcGaugeView {

    var phases: [SpeedMeasurementPhase] = [
        .initialize,
        .rtt,
        .download,
        .upload
    ]

    let speedUnits = [ // TODO: make translatable
        "0Mbps",
        "1Mbps",
        "10Mbps",
        "100Mbps",
        "1Gbps"
    ]

    ///
    override func draw(_ rect: CGRect) {
        super.draw(rect)

        guard let context = UIGraphicsGetCurrentContext() else {
            return
        }

        let centerPoint = CGPoint(x: bounds.midX, y: bounds.midY)

        let angleRad = CoreGraphicsHelper.deg2rad(angle)
        let fixedProgress = CGFloat(min(1, max(0, progress)))
        let fixedValue = CGFloat(min(1, max(0, value)))

        let anglePerPhase = CGFloat(angle/CGFloat(phases.count))
        
        context.saveGState()

        //

        context.translateBy(x: centerPoint.x, y: centerPoint.y)
        context.rotate(by: CoreGraphicsHelper.deg2rad(150))
        context.translateBy(x: -centerPoint.x, y: -centerPoint.y)

        // draw base
        context.setLineWidth(arcWidth)
        context.setStrokeColor(baseColor.cgColor)

        let (outerArcRadius, innerArcRadius) = calculateRadius()

        // outer arc
        context.addArc(center: centerPoint, radius: outerArcRadius, startAngle: 0, endAngle: angleRad, clockwise: false)
        context.strokePath()

        // inner arc
        context.addArc(center: centerPoint, radius: innerArcRadius, startAngle: 0, endAngle: angleRad, clockwise: false)
        context.strokePath()

        // draw progress
        context.setStrokeColor(progressColor.cgColor)

        let z = CGFloat(currentPhase.rawValue)

        let progressAngle = (z * anglePerPhase) + anglePerPhase * fixedProgress

        context.addArc(center: centerPoint, radius: outerArcRadius, startAngle: 0, endAngle: CoreGraphicsHelper.deg2rad(progressAngle), clockwise: false)
        context.strokePath()

        // draw value
        context.setStrokeColor(valueColor.cgColor)

        context.addArc(center: centerPoint, radius: innerArcRadius, startAngle: 0, endAngle: CoreGraphicsHelper.deg2rad(angle * fixedValue), clockwise: false)
        context.strokePath()

        //////////

        context.setStrokeColor(textColor.cgColor)
        context.setLineWidth(2)

        for i: CGFloat in stride(from: anglePerPhase, to: CGFloat(angle), by: anglePerPhase) {
            let phi = CoreGraphicsHelper.deg2rad(i)

            let p1 = CoreGraphicsHelper.getPointOnCircle(radius: outerArcRadius + arcWidth/2, center: centerPoint, phi: phi)
            let p2 = CoreGraphicsHelper.getPointOnCircle(radius: outerArcRadius - arcWidth/2, center: centerPoint, phi: phi)

            context.move(to: p1)
            context.addLine(to: p2)
            context.strokePath()
        }

        for i: CGFloat in stride(from: CGFloat(angle/4), to: CGFloat(angle), by: CGFloat(angle/4)) {
            let phi = CoreGraphicsHelper.deg2rad(i)

            let p1 = CoreGraphicsHelper.getPointOnCircle(radius: innerArcRadius - arcWidth/4, center: centerPoint, phi: phi)
            let p2 = CoreGraphicsHelper.getPointOnCircle(radius: innerArcRadius - arcWidth/2, center: centerPoint, phi: phi)

            context.move(to: p1)
            context.addLine(to: p2)
            context.strokePath()
        }

        //////////

        context.translateBy(x: centerPoint.x, y: centerPoint.y)
        context.scaleBy(x: 1, y: -1)

        var c = 0
        for i in stride(from: 0, to: angle, by: anglePerPhase) {
            let a = CoreGraphicsHelper.deg2rad(-CGFloat(i + (anglePerPhase)/2))

            CoreGraphicsHelper.centreArcPerpendicular(
                text: phases[c].localizedString,
                context: context,
                radius: outerArcRadius,
                angle: a,
                colour: textColor,
                font: UIFont.systemFont(ofSize: 16),
                clockwise: true
            )

            c += 1
        }

        let an = angle + 30
        c = 0
        for i in stride(from: 0, to: an, by: an/CGFloat(speedUnits.count)) {
            let a = CoreGraphicsHelper.deg2rad(+(CGFloat((an-angle)/2))-CGFloat(i + (an/CGFloat(speedUnits.count))/2))

            CoreGraphicsHelper.centreArcPerpendicular(
                text: speedUnits[c],
                context: context,
                radius: innerArcRadius + 2,
                angle: a,
                colour: textColor,
                font: UIFont.systemFont(ofSize: 10),
                clockwise: true
            )

            c += 1
        }

        context.restoreGState()
    }
}
