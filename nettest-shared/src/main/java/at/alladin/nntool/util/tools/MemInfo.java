/*******************************************************************************
 * Copyright 2015-2019 alladin-IT GmbH
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
// based on: https://raw.githubusercontent.com/alladin-IT/open-rmbt/master/RMBTUtil/src/main/java/at/alladin/rmbt/util/tools/MemInfo.java
package at.alladin.nntool.util.tools;

import java.util.Map;

public interface MemInfo {

    public interface MemInfoType {
        public Long getMemoryValue(final MemInfo memInfo);
    }

	public final static Long UNKNOWN = null;

	public void update();
	
	public Map<String, Long> getMemoryMap();
	
	public Long getTotalMem();
	
	public Long getFreeMem();

    public float calculateMemoryUsage();

    public Long getValue(final MemInfoType memInfoType);
}
