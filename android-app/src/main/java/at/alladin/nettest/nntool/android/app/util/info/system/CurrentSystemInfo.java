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

package at.alladin.nettest.nntool.android.app.util.info.system;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CurrentSystemInfo {

    public static class MemUsage {
        final Float memoryUsage;
        final Long memoryTotal;
        final Long memoryFree;

        public MemUsage(final Long memoryTotal, final Long memoryFree) {
            this.memoryFree = memoryFree;
            this.memoryTotal = memoryTotal;
            if (memoryFree != null && memoryTotal != null) {
                this.memoryUsage = 1f - ((float) memoryFree / (float) memoryTotal);
            }
            else {
                this.memoryUsage = null;
            }
        }

        public Float getMemoryUsage() {
            return memoryUsage;
        }

        public Long getMemoryTotal() {
            return memoryTotal;
        }

        public Long getMemoryFree() {
            return memoryFree;
        }

        @Override
        public String toString() {
            return "MemUsage{" +
                    "memoryUsage=" + memoryUsage +
                    ", memoryTotal=" + memoryTotal +
                    ", memoryFree=" + memoryFree +
                    '}';
        }
    }

    private final Float cpuUsage;

    private final MemUsage memUsage;

    private final long timeNs = System.nanoTime();

    public CurrentSystemInfo(final Float cpuUsage, final MemUsage memUsage) {
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
    }

    public Float getCpuUsage() {
        return cpuUsage;
    }

    public MemUsage getMemUsage() {
        return memUsage;
    }

    public long getTimeNs() {
        return timeNs;
    }

    @Override
    public String toString() {
        return "CurrentSystemInfo{" +
                "cpuUsage=" + cpuUsage +
                ", memUsage=" + memUsage +
                ", timeNs=" + timeNs +
                '}';
    }
}
