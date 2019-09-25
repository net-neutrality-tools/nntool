// MeasurementAgentKit: InterfaceTrafficInfo.swift, created on 09.07.19
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

public enum InterfaceTrafficClassification: Int {
    case none = 0 // = 0..1249
    case low  = 1 // = 1250..12499
    case mid  = 2 // = 12500..124999
    case high = 3 // = 125000..UInt64.max

    ///
    public static func classify(_ bps: UInt32) -> InterfaceTrafficClassification {
        switch bps {
        case 1250...12499: return .low
        case 12500...124999: return .mid
        case 125000...UInt32.max: return .high
        default: return .none
        }
    }
}

public struct InterfaceTraffic {
    var tx: UInt32
    var rx: UInt32

    public mutating func append(_ other: InterfaceTraffic?) {
        guard let other = other else {
            return
        }

        if UInt64(tx) + UInt64(other.tx) > UInt64(UInt32.max) {
            tx = UInt32(UInt64(tx) + UInt64(other.tx) - UInt64(UInt32.max))
        } else {
            tx += other.tx
        }

        if UInt64(rx) + UInt64(other.rx) > UInt64(UInt32.max) {
            rx = UInt32(UInt64(rx) + UInt64(other.rx) - UInt64(UInt32.max))
        } else {
            rx += other.rx
        }
    }

    public func differenceTo(_ previous: InterfaceTraffic) -> InterfaceTraffic {
        var txDiff: UInt32 = 0
        if tx < previous.tx {
            txDiff += UInt32.max - previous.tx + tx
        } else {
            txDiff = tx - previous.tx
        }

        var rxDiff: UInt32 = 0
        if rx < previous.rx {
            rxDiff += UInt32.max - previous.rx + rx
        } else {
            rxDiff = rx - previous.rx
        }

        return InterfaceTraffic(tx: txDiff, rx: rxDiff)
    }

    public func classify() -> (InterfaceTrafficClassification, InterfaceTrafficClassification) {
        return (InterfaceTrafficClassification.classify(tx), InterfaceTrafficClassification.classify(rx))
    }
}

public struct InterfaceCategoryTraffic {
    let wwan: InterfaceTraffic
    let wifi: InterfaceTraffic

    public func differenceTo(_ previous: InterfaceCategoryTraffic) -> InterfaceCategoryTraffic {
        return InterfaceCategoryTraffic(wwan: wwan.differenceTo(previous.wwan), wifi: wifi.differenceTo(previous.wifi))
    }
}

public class InterfaceTrafficInfo {

    public class func readNetworkInterfaceTraffic() -> [String: InterfaceTraffic]? {
        var addrs: UnsafeMutablePointer<ifaddrs>?
        guard getifaddrs(&addrs) == 0 else {
            return nil
        }

        var trafficDict = [String: InterfaceTraffic]()

        var cursor = addrs
        while cursor != nil {
            defer {
                cursor = cursor?.pointee.ifa_next
            }

            guard let iface = cursor?.pointee else {
                continue
            }

            let family = iface.ifa_addr.pointee.sa_family

            guard family == AF_LINK else {
                continue
            }

            let name = String(cString: iface.ifa_name)

            let data = unsafeBitCast(iface.ifa_data, to: UnsafeMutablePointer<if_data>.self)

            trafficDict[name] = InterfaceTraffic(tx: data.pointee.ifi_obytes, rx: data.pointee.ifi_ibytes)
        }

        freeifaddrs(addrs)

        return trafficDict
    }

    public class func getNetworkInterfaceTrafficByCategory() -> InterfaceCategoryTraffic? {
        guard let trafficDict = readNetworkInterfaceTraffic() else {
            return nil
        }

        var wwanTraffic = InterfaceTraffic(tx: 0, rx: 0)
        var wifiTraffic = InterfaceTraffic(tx: 0, rx: 0)

        trafficDict.forEach { (key, value) in
            if key.hasPrefix("en") {
                wifiTraffic.append(value)
            } else if key.hasPrefix("pdp_ip") {
                wwanTraffic.append(value)
            }
        }

        return InterfaceCategoryTraffic(wwan: wwanTraffic, wifi: wifiTraffic)
    }

    public class func getWwanAndWifiNetworkInterfaceTraffic() -> InterfaceTraffic? {
        guard let currentValues = getNetworkInterfaceTrafficByCategory() else {
            return nil
        }

        var i = InterfaceTraffic(
            tx: currentValues.wwan.tx,
            rx: currentValues.wwan.rx
        )

        i.append(InterfaceTraffic(
            tx: currentValues.wifi.tx,
            rx: currentValues.wifi.rx
        ))

        return i
    }

    public class func getNetworkInterfaceTrafficDifferenceByCategory(_ lastValues: InterfaceCategoryTraffic) -> InterfaceCategoryTraffic? {
        guard let currentValues = getNetworkInterfaceTrafficByCategory() else {
            return nil
        }

        return currentValues.differenceTo(lastValues)
    }
}
