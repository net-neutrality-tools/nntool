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

package at.alladin.nettest.service.loadbalancing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The load balancing service YAML configuration object.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "loadbalancer", ignoreUnknownFields = true)
public class LoadbalancerServiceProperties {

	private final static long DEFAULT_DELAY = 10000; //10s

	private Boolean enabled = false;

	private Long delay = DEFAULT_DELAY;
	
	private Integer failsAllowed = 0;
	
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}

	public Integer getFailsAllowed() {
		return failsAllowed;
	}

	public void setFailsAllowed(Integer failsAllowed) {
		this.failsAllowed = failsAllowed;
	}
}
