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
public class CpuUsageInfo {

    private var lock = os_unfair_lock_s()

    private var prevCpuUsage: Float?

    private var prevCpuInfo: processor_info_array_t?
    private var numPrevCpuInfo: mach_msg_type_number_t = 0

    public init() {

    }

    deinit {
        deallocatePrev()
    }

    private func deallocatePrev() {
        if let prev = prevCpuInfo {
            let prevSize: size_t = MemoryLayout<integer_t>.stride * Int(numPrevCpuInfo)

            // vm_address_t(bitPattern: prev) is the preferred way in newer Swift versions but fails to
            // compile for "Generic iOS Device"
            // (see https://stackoverflow.com/questions/26562731/c-array-memory-deallocation-from-swift ).
            //vm_deallocate(mach_task_self_, vm_address_t(bitPattern: prev), vm_size_t(prevSize))
            vm_deallocate(mach_task_self_, unsafeBitCast(prev, to: vm_address_t.self), vm_size_t(prevSize))
        }
    }

    // TODO: we could also report usage per cpu core and actual value instead of just percent

    public func getCurrentCpuUsagePercent() -> Double? {
        var numCPUs: UInt = 0

        let mibKeys: [Int32] = [CTL_HW, HW_NCPU]

        mibKeys.withUnsafeBufferPointer { mib in
            var sizeOfNumCPUs = MemoryLayout<uint>.size
            let status = sysctl(processor_info_array_t(mutating: mib.baseAddress), 2, &numCPUs, &sizeOfNumCPUs, nil, 0)

            if status != 0 {
                numCPUs = 1
            }
        }

        var numCPUsU: natural_t = 0

        var cpuInfo: processor_info_array_t?
        var numCpuInfo: mach_msg_type_number_t = 0

        let ret: kern_return_t = host_processor_info(mach_host_self(), PROCESSOR_CPU_LOAD_INFO, &numCPUsU, &cpuInfo, &numCpuInfo)

        if ret != KERN_SUCCESS {
            return nil
        }

        guard let info = cpuInfo else {
            return nil
        }

        var total: Float = 0

        os_unfair_lock_lock(&lock)

        for p in 0..<Int32(numCPUs) {
            var inUse: Int32
            var coreTotal: Int32

            if let prev = prevCpuInfo {
                inUse = info[Int(CPU_STATE_MAX * p + CPU_STATE_USER)]
                    + info[Int(CPU_STATE_MAX * p + CPU_STATE_SYSTEM)]
                    + info[Int(CPU_STATE_MAX * p + CPU_STATE_NICE)]
                    - prev[Int(CPU_STATE_MAX * p + CPU_STATE_USER)]
                    - prev[Int(CPU_STATE_MAX * p + CPU_STATE_SYSTEM)]
                    - prev[Int(CPU_STATE_MAX * p + CPU_STATE_NICE)]

                coreTotal = inUse
                    + info[Int(CPU_STATE_MAX * p + CPU_STATE_IDLE)]
                    - prev[Int(CPU_STATE_MAX * p + CPU_STATE_IDLE)]
            } else {
                inUse = info[Int(CPU_STATE_MAX * p + CPU_STATE_USER)] +
                    info[Int(CPU_STATE_MAX * p + CPU_STATE_SYSTEM)] +
                    info[Int(CPU_STATE_MAX * p + CPU_STATE_NICE)]

                coreTotal = inUse + info[Int(CPU_STATE_MAX * p + CPU_STATE_IDLE)]
            }

            total += Float(inUse) / Float(coreTotal)
        }

        let cpuUsagePercent = Double(total) / Double(numCPUs)

        os_unfair_lock_unlock(&lock)

        deallocatePrev()

        prevCpuInfo = cpuInfo
        numPrevCpuInfo = numCpuInfo

        cpuInfo = nil
        numCpuInfo = 0

        return cpuUsagePercent
    }
}
