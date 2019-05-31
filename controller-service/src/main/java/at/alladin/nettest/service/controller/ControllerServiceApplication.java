package at.alladin.nettest.service.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import at.alladin.nettest.service.controller.config.ControllerServiceProperties;
import at.alladin.nettest.shared.server.helper.spring.DevelopmentWebCorsConfiguration;
import at.alladin.nettest.shared.server.helper.spring.SpringApplicationHelper;
import at.alladin.nettest.shared.server.storage.couchdb.config.EnableCouchDbStorage;

/**
 * The controller's main class which allows the controller to be started as stand-alone Java application or inside a servlet container.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@SpringBootApplication
@EnableConfigurationProperties({
	ControllerServiceProperties.class
})
@ComponentScan({
	"at.alladin.nettest.service.controller", 
	"at.alladin.nettest.shared.server.web.api.v1"
})
@Import({ DevelopmentWebCorsConfiguration.class })
@EnableCouchDbStorage
public class ControllerServiceApplication extends SpringBootServletInitializer {

	/**
	 * 
	 */
	private static final String CONFIGURATION_DIRECTORY_NAME = "controller-service";
	
	/**
	 * 
	 */
	private static final Class<?> APPLICATION_CLASS = ControllerServiceApplication.class;
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.boot.context.web.SpringBootServletInitializer#configure(org.springframework.boot.builder.SpringApplicationBuilder)
	 */
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return SpringApplicationHelper.configure(CONFIGURATION_DIRECTORY_NAME, application, APPLICATION_CLASS);
    }
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SpringApplicationHelper.runSpingApplication(CONFIGURATION_DIRECTORY_NAME, args, APPLICATION_CLASS);
	}
}
