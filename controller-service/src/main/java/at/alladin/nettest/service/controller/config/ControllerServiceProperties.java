package at.alladin.nettest.service.controller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The controller service YAML configuration object.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "controller", ignoreUnknownFields = true)
public class ControllerServiceProperties {

}
