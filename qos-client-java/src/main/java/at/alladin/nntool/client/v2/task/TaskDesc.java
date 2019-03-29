/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
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

package at.alladin.nntool.client.v2.task;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import at.alladin.nntool.client.TestParameter;

/**
 * 
 * @author lb
 *
 */
public class TaskDesc extends TestParameter implements Serializable {
	
	public final static String QOS_TEST_IDENTIFIER_KEY = "qostest";

	/**
	 * 
	 */
	private final Map<String, Object> params;

	public TaskDesc() {
		super();
		params = new HashMap<>();
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param encryption
	 * @param token
	 * @param duration
	 * @param numThreads
	 * @param startTime
	 */
	public TaskDesc(String host, int port, boolean encryption, String token,
			int duration, int numThreads, int numPings, long startTime, Map<String, Object> params) {
		super(host, port, encryption, token, duration, numThreads, numPings, startTime);
		this.params = params;
	}
	
	public TaskDesc(String host, int port, boolean encryption, String token,
			int duration, int numThreads, int numPings, long startTime, Map<String, Object> params, String qosTestId) {
		this(host, port, encryption, token, duration, numThreads, numPings, startTime, params);
		params.put(QOS_TEST_IDENTIFIER_KEY, qosTestId);
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return "TaskDesc [params=" + params + "]";
	}
}
