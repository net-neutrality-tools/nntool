package at.alladin.nettest.service.collector;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import at.alladin.nettest.service.collector.config.CollectorServiceProperties;
import at.alladin.nettest.shared.server.helper.spring.SpringApplicationHelper;

/**
 * The collector's main class which allows the collector to be started as stand-alone Java application or inside a servlet container.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@SpringBootApplication
@EnableConfigurationProperties({
	CollectorServiceProperties.class
})
//@ComponentScan({"at.alladin.nettest.service.collector", "at.alladin.nettest.shared.server.service"})
public class CollectorServiceApplication extends SpringBootServletInitializer {

	/**
	 * 
	 */
	private static final String CONFIGURATION_DIRECTORY_NAME = "collector-service";
	
	/**
	 * 
	 */
	private static final Class<?> APPLICATION_CLASS = CollectorServiceApplication.class;
	
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
