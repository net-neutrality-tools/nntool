// ios-app: QoSGroupProgressCell.swift, created on 26.03.19
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
class QoSGroupProgressCell: UITableViewCell {

    ///
    @IBOutlet private var progressView: QoSProgressView?

    ///
    @IBOutlet private var groupNameLabel: UILabel?

    ///
    var animatesProgressChanges = true

    ///
    var progress: Float {
        get {
            return progressView?.progress ?? 0
        }
        set {
            progressView?.setProgress(newValue, animated: animatesProgressChanges)
        }
    }

    ///
    var groupName: String? {
        get {
            return groupNameLabel?.text
        }
        set {
            groupNameLabel?.text = newValue
        }
    }

    ///
    override func prepareForReuse() {
        //progress = 0
        progressView?.setProgress(0, animated: false)
    }
}
