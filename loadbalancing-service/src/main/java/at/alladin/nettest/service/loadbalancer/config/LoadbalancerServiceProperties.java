package at.alladin.nettest.service.loadbalancer.config;

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
