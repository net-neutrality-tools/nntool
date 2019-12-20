/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.alladin.nntool.util.tools.CpuStat;
import at.alladin.nntool.util.tools.ToolUtils;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CpuInfoImpl extends CpuStat {

    public enum CpuMemClassificationEnum {

        LOW(0, 50),
        MID(50, 75),
        HIGH(75, Float.MAX_VALUE),
        UNKNOWN(Float.MIN_VALUE, Float.MIN_VALUE);

        protected float min;
        protected float max;

        CpuMemClassificationEnum(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        /**
         *
         * @param value
         * @return
         */
        public static CpuMemClassificationEnum classify(float value) {
            for (CpuMemClassificationEnum e : CpuMemClassificationEnum.values()) {
                if (e.getMin() <= value && e.getMax() > value) {
                    return e;
                }
            }

            return UNKNOWN;
        }
    }

    private final static String CPUSTAT_PATH = "/proc/stat";

    private final static Pattern CPU_PATTERN = Pattern.compile("cpu[^0-9]([\\s0-9]*)");

    private final static Pattern CPU_CORE_PATTERN = Pattern.compile("cpu([0-9]+)([\\s0-9]*)");

    public CpuUsage getCurrentCpuUsage(boolean getByCore) {
        CpuUsage cpuUsage = new CpuUsage();

        String stat = ToolUtils.readFromProc(CPUSTAT_PATH);

        if (getByCore) {
            Matcher m = CPU_CORE_PATTERN.matcher(stat);
            while (m.find()) {
                try {
                    int core = Integer.parseInt(m.group(1));
                    String[] cpu = m.group(2).trim().split(" ");

                    cpuUsage.getCoreUsageList().add(new
                            CpuUsage.CoreUsage(core, Integer.parseInt(cpu[0]),
                            Integer.parseInt(cpu[1]), Integer.parseInt(cpu[2]),
                            Integer.parseInt(cpu[3]), Integer.parseInt(cpu[4]),
                            Integer.parseInt(cpu[5]), Integer.parseInt(cpu[6])));
                }
                catch (final NumberFormatException e) {
                    e.printStackTrace();
                    //ignore if something is wrong
                }
            }
        } else {
            Matcher m = CPU_PATTERN.matcher(stat);
            while (m.find()) {
                String[] cpu = m.group(1).trim().split(" ");
                try {
                    cpuUsage.getCoreUsageList().add(new
                            CpuUsage.CoreUsage(0, Integer.parseInt(cpu[0]),
                            Integer.parseInt(cpu[1]), Integer.parseInt(cpu[2]),
                            Integer.parseInt(cpu[3]), Integer.parseInt(cpu[4]),
                            Integer.parseInt(cpu[5]), Integer.parseInt(cpu[6])));
                }
                catch (final NumberFormatException e) {
                    e.printStackTrace();
                    //ignore if something is wrong
                }
            }
        }

        return cpuUsage;
    }
}
