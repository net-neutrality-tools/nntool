// ios-app: UIViewController+WebBrowser.swift, created on 27.03.19
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
import WebBrowser

///
extension UIViewController {

    ///
    func embeddWebBrowserWithUrlString(_ urlString: String, delegate: WebBrowserDelegate? = nil) {
        let webBrowserViewController = createCustomWebBrowserViewController(urlString, delegate: delegate)

        addChild(webBrowserViewController)
        view.addSubview(webBrowserViewController.view)
    }

    ///
    func presentWebBrowserWithUrlString(_ urlString: String, delegate: WebBrowserDelegate? = nil) {
        let webBrowserViewController = createCustomWebBrowserViewController(urlString, delegate: delegate)
        let navigationWebBrowser = WebBrowserViewController.rootNavigationWebBrowser(webBrowser: webBrowserViewController)

        // Change "done" text to x icon
        //navigationWebBrowser.navigationBar.items?.forEach { $0.rightBarButtonItems?.forEach { $0.icon = .cross } }

        present(navigationWebBrowser, animated: true, completion: nil)
    }

    ///
    private func createCustomWebBrowserViewController(_ urlString: String, delegate: WebBrowserDelegate?) -> WebBrowserViewController {
        let webBrowserViewController = WebBrowserViewController(/*configuration: <#T##WKWebViewConfiguration#>*/)

        webBrowserViewController.delegate = delegate

        //webBrowserViewController.language = .english
        webBrowserViewController.tintColor = APP_TINT_COLOR
        //webBrowserViewController.barTintColor = ...
        webBrowserViewController.isToolbarHidden = true
        //webBrowserViewController.isShowActionBarButton = true
        //webBrowserViewController.toolbarItemSpace = 50
        webBrowserViewController.isShowURLInNavigationBarWhenLoading = false
        webBrowserViewController.isShowPageTitleInNavigationBar = true
        //webBrowserViewController.customApplicationActivities = ...

        webBrowserViewController.loadURLString(urlString)

        return webBrowserViewController
    }

    ///
    @IBAction func presentHelp() {
        presentWebBrowserWithUrlString("https://net-neutrality.tools")
    }
}
