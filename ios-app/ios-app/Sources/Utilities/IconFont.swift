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

///
enum IconFont: String {
    case watch = "b"
    case history = "i"
    case map = "j"
    case statistics = "l"
    case help = "m"
    case about = "o"
    case settings = "p"
    case rtt = "q"
    case down = "r"
    case up = "s"
    case cellular = "t"
    case wifi = "u"
    case check = "v"
    case cross = "w"
    case location = "x"
    case voip = "à"
    case dns = "á"
    case arrowAverage = "ä"
    case webRendering = "è"
    case httpProxy = "é"
    case qos = "ë"
    case udp = "ì"
    case nonTransparent = "í"
    case adjustment = "ò"
    case tcp = "ó"
    case echoProtocol = "û"
    case filter = "ù"
    case traceroute = "ú"
    case logoInverse = "A"
    case watchInverse = "B"
    case trafficIn = "I"
    case trafficOut = "J"
    case hourglass = "O"
    case androidShare = "À"
    case iosShare = "Á"

    ///
    func repeating(_ count: Int) -> String {
        return String(repeating: self.rawValue, count: count)
    }
}
