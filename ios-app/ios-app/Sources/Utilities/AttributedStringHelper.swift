// ios-app: AttributedStringHelper.swift, created on 10.07.19
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

class AttributedStringHelper {

    public class func repeatedColoredIconString(_ icon: IconFont, count: Int, activeCount: Int, ltr: Bool, activeColor: UIColor = BEREC_RED, inactiveColor: UIColor = BEREC_LIGHT_GRAY) -> NSAttributedString {

        let str = NSMutableAttributedString(string: icon.repeating(count))
        str.addAttribute(.foregroundColor, value: inactiveColor, range: NSRange(location: 0, length: count))

        var location = 0
        var length = activeCount

        if !ltr {
            location = count - activeCount
        }

        str.addAttribute(.foregroundColor, value: activeColor, range: NSRange(location: location, length: length))

        return str
    }
}
