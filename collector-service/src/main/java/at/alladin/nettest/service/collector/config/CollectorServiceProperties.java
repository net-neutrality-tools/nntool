package at.alladin.nettest.service.collector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The collector service YAML configuration object.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "collector", ignoreUnknownFields = true)
public class CollectorServiceProperties {

    private String systemUuid;

    public String getSystemUuid() {
        return systemUuid;
    }

    public void setSystemUuid(String systemUuid) {
        this.systemUuid = systemUuid;
    }
}
