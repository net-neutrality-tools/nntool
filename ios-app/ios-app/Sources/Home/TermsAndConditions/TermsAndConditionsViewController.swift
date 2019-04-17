// ios-app: TermsAndConditionsViewController.swift, created on 19.03.19
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
import WebKit

/// This view controller displays the Terms and Conditions.
class TermsAndConditionsViewController: UIViewController {

    @IBOutlet var declineButtonItem: UIBarButtonItem?
    @IBOutlet var agreeButtonItem: UIBarButtonItem?

    ///
    override func viewDidLoad() {
        super.viewDidLoad()

        let font = UIFont.systemFont(ofSize: UIFont.buttonFontSize)

        declineButtonItem?.setTitleTextAttributes([NSAttributedString.Key.font: font], for: .normal)
        declineButtonItem?.setTitleTextAttributes([NSAttributedString.Key.font: font], for: .selected)
        declineButtonItem?.setTitleTextAttributes([NSAttributedString.Key.font: font], for: .disabled)

        agreeButtonItem?.setTitleTextAttributes([NSAttributedString.Key.font: font], for: .normal)
        agreeButtonItem?.setTitleTextAttributes([NSAttributedString.Key.font: font], for: .selected)
        agreeButtonItem?.setTitleTextAttributes([NSAttributedString.Key.font: font], for: .disabled)

        // TODO: wait until webview did finish loading to make decline/agree touchable
    }

    /// Show a popup with an error message and options to retry or close the App.
    func showErrorPopup() {
        let alert = UIAlertController(title: "Registration error", message: "Agent could not be registered, TODO: message", preferredStyle: .alert)

        // TODO: nslocalizedstring

        alert.addAction(UIAlertAction(title: "Retry", style: .default, handler: { _ in
            self.agree()
        }))

        alert.addAction(UIAlertAction(title: "Close App", style: .destructive, handler: { _ in
            self.decline()
        }))

        self.present(alert, animated: true, completion: nil)
    }

    /// Try to register Measurement Agent against control service.
    /// If successful, dismiss this screen to go to the start page.
    /// If there was an error, e.g. control service could not be reached, display an alert view asking the user to retry or close the App.
    @IBAction func agree() {
        let progressAlert = UIAlertController.createLoadingAlert(title: "Registering agent")
        self.present(progressAlert, animated: true, completion: nil)

        MEASUREMENT_AGENT.register(success: {
            DispatchQueue.main.async {
                progressAlert.dismiss(animated: true) {
                    self.dismiss(animated: true, completion: nil)
                }
            }
        }, failure: {
            DispatchQueue.main.async {
                progressAlert.dismiss(animated: false) {
                    self.showErrorPopup()
                }
            }
        })
    }

    /// Close the App if Terms and Conditions aren't accepted by the user.
    @IBAction func decline() {
        exit(EXIT_FAILURE)
    }
}
