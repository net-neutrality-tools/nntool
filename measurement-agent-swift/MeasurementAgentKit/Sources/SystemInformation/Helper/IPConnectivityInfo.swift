// MeasurementAgentKit: IPConnectivityInfo.swift, created on 09.07.19
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

public struct IPStatus {
    public var localAddress: String?
    public var publicAddress: String?

    public var hasInternetConnection = false

    public var isNat: Bool {
        return localAddress != publicAddress
    }
}

extension IPStatus: CustomDebugStringConvertible {

    public var debugDescription: String {
        return "[local: \(String(describing: localAddress)), public: \(String(describing: publicAddress)), conn: \(hasInternetConnection), nat: \(isNat)]"
    }
}

public struct ConnectivityStatus {
    public var ipv4: IPStatus?
    public var ipv6: IPStatus?
}

public class IPConnectivityInfo {

    let controlServiceV4: ControlService
    let controlServiceV6: ControlService

    init(controlServiceV4: ControlService, controlServiceV6: ControlService) {
        self.controlServiceV4 = controlServiceV4
        self.controlServiceV6 = controlServiceV6
    }

    public func checkIPv4Connectivity() -> IPStatus? {
        let semaphore = DispatchSemaphore(value: 0)

        var ipv4Status = IPStatus()

        ipv4Status.localAddress = getLocalIPv4Address()

        DispatchQueue.main.async {
            self.controlServiceV4.getIp(onSuccess: { response in
                guard let version = response.ipVersion, version == .ipv4 else {
                    return
                }

                ipv4Status.publicAddress = response.ipAddress
                ipv4Status.hasInternetConnection = true

                semaphore.signal()
            }, onFailure: { _ in
                semaphore.signal()
            })
        }

        _ = semaphore.wait(timeout: .now() + .seconds(2))

        return ipv4Status
    }

    public func checkIPv6Connectivity() -> IPStatus? {
        let semaphore = DispatchSemaphore(value: 0)

        var ipv6Status = IPStatus()

        ipv6Status.localAddress = getLocalIPv6Address()

        DispatchQueue.main.async {
            self.controlServiceV6.getIp(onSuccess: { response in
                guard let version = response.ipVersion, version == .ipv6 else {
                    return
                }

                ipv6Status.publicAddress = response.ipAddress
                ipv6Status.hasInternetConnection = true

                semaphore.signal()
            }, onFailure: { _ in
                semaphore.signal()
            })
        }

        _ = semaphore.wait(timeout: .now() + .seconds(2))

        return ipv6Status
    }

    public func getLocalIPv4Address() -> String? {
        return getLocalActiveIpAddress(family: AF_INET)
    }

    public func getLocalIPv6Address() -> String? {
        return getLocalActiveIpAddress(family: AF_INET6)
    }

    public func getLocalActiveIpAddress(family: Int32) -> String? {
        var addrs: UnsafeMutablePointer<ifaddrs>?
        guard getifaddrs(&addrs) == 0 else {
            return nil
        }

        var address: String?

        var cursor = addrs
        while cursor != nil {
            defer {
                cursor = cursor?.pointee.ifa_next
            }

            guard let iface = cursor?.pointee else {
                continue
            }

            let saFamily = iface.ifa_addr.pointee.sa_family

            guard saFamily == family else {
                continue
            }

            let flags = Int32(iface.ifa_flags)

            guard flags & (IFF_UP|IFF_RUNNING|IFF_LOOPBACK) == (IFF_UP|IFF_RUNNING) else {
                continue
            }

            // TODO: check for interface type (name.hasPrefix("en") => wifi, name.hasPrefix("pdp_ip") => wwan)

            var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
            if getnameinfo(
                    iface.ifa_addr, socklen_t(iface.ifa_addr.pointee.sa_len),
                    &hostname, socklen_t(hostname.count),
                    nil, socklen_t(0), NI_NUMERICHOST
                ) == 0 {

                address = String(cString: hostname)
                break
            }
        }

        freeifaddrs(addrs)

        return address
    }
}
