// ios-app: CustomNavigationBarViewController.swift, created on 26.03.19
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
class CustomNavigationBarViewController: UIViewController {

    ///
    @IBOutlet var leftBarItem: UIBarButtonItem?

    ///
    @IBOutlet var rightBarItem: UIBarButtonItem?

    ///
    override func viewDidLoad() {
        super.viewDidLoad()

        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
    }

    ///
    func hideNavigationItems() {
        navigationItem.setLeftBarButton(nil, animated: true)
        navigationItem.setRightBarButton(nil, animated: true)
        navigationItem.setHidesBackButton(true, animated: false)
    }

    ///
    func showNavigationItems() {
        navigationItem.setLeftBarButton(leftBarItem, animated: true)
        navigationItem.setRightBarButton(rightBarItem, animated: true)
        navigationItem.setHidesBackButton(false, animated: false)
    }
}
