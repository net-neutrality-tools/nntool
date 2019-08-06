// ios-app: UIAlertController+ProgressIndicator.swift, created on 17.04.19
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

extension UIAlertController {

    class func createLoadingAlert(title: String, message: String = "") -> UIAlertController {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addActivityIndicator()

        return alert
    }

    func addActivityIndicator() {
        guard let v = view else {
            return
        }

        let activityIndicator = UIActivityIndicatorView(style: .gray)

        activityIndicator.hidesWhenStopped = true
        activityIndicator.isUserInteractionEnabled = false

        v.addSubview(activityIndicator)

        activityIndicator.translatesAutoresizingMaskIntoConstraints = false
        let xConstraint = NSLayoutConstraint(item: activityIndicator, attribute: .centerX, relatedBy: .equal, toItem: v, attribute: .centerX, multiplier: 1, constant: 0)
        let yConstraint = NSLayoutConstraint(item: activityIndicator, attribute: .centerY, relatedBy: .equal, toItem: v, attribute: .centerY, multiplier: 1.4, constant: 0)

        NSLayoutConstraint.activate([xConstraint, yConstraint])

        let height = NSLayoutConstraint(item: v, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1, constant: 80)
        v.addConstraint(height)

        activityIndicator.startAnimating()
    }
}
