package at.alladin.nettest.service.result.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The result service YAML configuration object.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "result", ignoreUnknownFields = true)
public class ResultServiceProperties {

}
