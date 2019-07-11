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

        // TODO: get local ipv4 address

        var ipv4Status = IPStatus()

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

        // TODO: get local ipv6 address

        var ipv6Status = IPStatus()

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

    /*private func getLocalIPv4Address() -> String? {
        
    }
    
    private func getLocalIPv6Address() -> String? {
        
    }*/
}
