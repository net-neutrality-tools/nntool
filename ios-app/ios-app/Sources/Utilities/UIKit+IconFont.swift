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
extension UILabel {

    ///
    var icon: IconFont? {
        get {
            guard let t = text else {
                return nil
            }

            return IconFont(rawValue: t)
        }
        set {
            text = newValue?.rawValue
        }
    }

    class func createIconLabel(icon: IconFont, frame: CGRect = CGRect(x: 0, y: 0, width: 26, height: 26), textColor: UIColor = UIColor.black, fontSize: CGFloat = 18) -> UILabel {
        let iconLabel = UILabel(frame: frame)
        iconLabel.font = R.font.berecIcons(size: fontSize)

        iconLabel.textColor = textColor
        iconLabel.icon = icon

        return iconLabel
    }
}

///
extension UIButton {

    ///
    func setIcon(_ icon: IconFont, for state: UIControl.State) {
        setTitle(icon.rawValue, for: state)
    }
}

///
extension UIBarButtonItem {

    private static let iconFontAttributes: [NSAttributedString.Key: Any] = [
        NSAttributedString.Key.font: R.font.berecIcons(size: 32)!, // !
        NSAttributedString.Key.foregroundColor: UINavigationBar.appearance().tintColor ?? UIColor.black
    ]

    ///
    var icon: IconFont? {
        get {
            guard let t = title else {
                return nil
            }

            return IconFont(rawValue: t)
        }
        set {
            title = newValue?.rawValue
        }
    }

    func applyIconFontAttributes() {
        setTitleTextAttributes(UIBarButtonItem.iconFontAttributes, for: .normal)
        setTitleTextAttributes(UIBarButtonItem.iconFontAttributes, for: .highlighted)
        setTitleTextAttributes(UIBarButtonItem.iconFontAttributes, for: .disabled)
        //setTitleTextAttributes(UIBarButtonItem.iconFontAttributes, for: .selected)
        setTitleTextAttributes(UIBarButtonItem.iconFontAttributes, for: .focused)
    }
}

/*extension UINavigationBar {

    func applyIconFontAttributes() {
        items?.forEach { $0.rightBarButtonItems?.forEach { $0.applyIconFontAttributes() } }
    }
}*/

extension UINavigationItem {

    func applyIconFontAttributes() {
        rightBarButtonItems?.forEach { $0.applyIconFontAttributes() }
    }
}
