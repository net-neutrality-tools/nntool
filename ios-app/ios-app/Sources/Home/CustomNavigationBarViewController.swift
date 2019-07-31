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
    private var savedRightBarButtonItems: [UIBarButtonItem]?

    /*var tabBarEnabled: Bool = true {
        didSet(enabled) {
            tabBarController?.tabBar.items?.forEach { $0.isEnabled = enabled }
        }
    }*/

    ///
    override func viewDidLoad() {
        super.viewDidLoad()

        // view controllers in tabBar should not be customizable
        tabBarController?.customizableViewControllers = []

        navigationItem.applyIconFontAttributes()

        // hide navigationBar shadow image
        navigationController?.navigationBar.shadowImage = UIImage()

        navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)
    }

    ///
    func hideNavigationItems() {
        savedRightBarButtonItems = navigationItem.rightBarButtonItems

        navigationItem.setRightBarButtonItems(nil, animated: true)
        navigationItem.setHidesBackButton(true, animated: false)

        tabBarController?.tabBar.isUserInteractionEnabled = false
        //tabBarEnabled = false
    }

    ///
    func showNavigationItems() {
        navigationItem.setRightBarButtonItems(savedRightBarButtonItems, animated: true)
        navigationItem.setHidesBackButton(false, animated: false)

        tabBarController?.tabBar.isUserInteractionEnabled = true
        //tabBarEnabled = true
    }
}
