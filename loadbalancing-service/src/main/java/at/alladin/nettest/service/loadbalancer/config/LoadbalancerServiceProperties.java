package at.alladin.nettest.service.loadbalancer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The result service YAML configuration object.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "result", ignoreUnknownFields = true)
public class LoadbalancerServiceProperties {
	
	private String settingsUuid;

	public String getSettingsUuid() {
		return settingsUuid;
	}

	public void setSettingsUuid(String settingsUuid) {
		this.settingsUuid = settingsUuid;
	}
}
