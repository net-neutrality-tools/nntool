// ios-app: HomeScreenDeviceInfoView.swift, created on 26.03.19
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
@IBDesignable class HomeScreenDeviceInfoView: NibView {

    @IBOutlet var deviceInfoStackView: UIStackView?
    
    @IBOutlet var cpuValueLabel: UILabel?
    @IBOutlet var memValueLabel: UILabel?

    @IBOutlet var ipv4ValueLabel: UILabel?
    @IBOutlet var ipv6ValueLabel: UILabel?

    @IBOutlet var trafficInValueLabel: UILabel?
    @IBOutlet var trafficOutValueLabel: UILabel?

    @IBOutlet var locationLabel: UILabel?
    @IBOutlet var locationInfoLabel: UILabel?

    @IBOutlet private var cpuTitleLabel: UILabel?
    @IBOutlet private var ramTitleLabel: UILabel?

    @IBOutlet private var ipv4TitleLabel: UILabel?
    @IBOutlet private var ipv6TitleLabel: UILabel?

    @IBOutlet private var trafficInTitleLabel: UILabel?
    @IBOutlet private var trafficOutTitleLabel: UILabel?

    override func awakeFromNib() {
        cpuTitleLabel?.text = R.string.localizable.homeDeviceInfoCpu()
        ramTitleLabel?.text = R.string.localizable.homeDeviceInfoRam()
        ipv4TitleLabel?.text = R.string.localizable.homeDeviceInfoIpv4()
        ipv6TitleLabel?.text = R.string.localizable.homeDeviceInfoIpv6()
        trafficInTitleLabel?.text = R.string.localizable.homeDeviceInfoTrafficIn()
        trafficOutTitleLabel?.text = R.string.localizable.homeDeviceInfoTrafficOut()

        // Change some UI constraints for iPhone 5s and SE
        if DeviceHelper.isSmalliPhone() {
            deviceInfoStackView?.spacing = 80
        }
    }

    func reset() {
        cpuValueLabel?.text = ""
        memValueLabel?.text = ""

        //ipv4ValueLabel?.icon = .cross
        //ipv6ValueLabel?.icon = .cross

        //trafficInValueLabel?.text = ""
        //trafficOutValueLabel?.text = ""

        locationLabel?.text = ""
        locationInfoLabel?.text = ""
    }
}
