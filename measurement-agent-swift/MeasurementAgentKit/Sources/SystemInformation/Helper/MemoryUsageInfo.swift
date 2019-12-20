/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
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
public class MemoryUsageInfo {

    private let totalMemoryBytes = Double(ProcessInfo.processInfo.physicalMemory)

    // TODO: we could also report actual value instead of just percent

    public init() {

    }

    public func getCurrentMemoryUsagePercent() -> Double? {
        var hostSize = mach_msg_type_number_t(MemoryLayout<vm_statistics_data_t>.stride / MemoryLayout<integer_t>.stride)

        let hostPort = mach_host_self()
        var pagesize: vm_size_t = 0

        host_page_size(hostPort, &pagesize)

        var vmStat = vm_statistics_data_t()
        let ret: kern_return_t = withUnsafeMutableBytes(of: &vmStat) {
            let boundBuffer = $0.bindMemory(to: Int32.self)
            return host_statistics(hostPort, HOST_VM_INFO, boundBuffer.baseAddress, &hostSize)
        }

        if ret != KERN_SUCCESS {
            return nil
        }

        let memUsed = Double(vmStat.active_count + vmStat.inactive_count + vmStat.wire_count) * Double(pagesize)

        return memUsed / totalMemoryBytes
    }
}
