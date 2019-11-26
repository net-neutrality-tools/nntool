package at.alladin.nettest.service.collector.opendata;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import at.alladin.nettest.service.collector.opendata.config.OpendataCollectorServiceProperties;
import at.alladin.nettest.shared.server.config.spring.DevelopmentWebCorsConfiguration;
import at.alladin.nettest.shared.server.config.spring.RestTemplateConfiguration;
import at.alladin.nettest.shared.server.helper.spring.SpringApplicationHelper;

/**
 * The opendata collector's main class which allows the collector to be started as stand-alone Java application or inside a servlet container.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {
	DataSourceAutoConfiguration.class // will be included based on property spring.datasource.url
})
@EnableConfigurationProperties({
	OpendataCollectorServiceProperties.class
})
@ComponentScan({
	"at.alladin.nettest.service.collector.opendata", 
	"at.alladin.nettest.shared.server.web.api.v1",
})
@Import({
	DevelopmentWebCorsConfiguration.class,
	RestTemplateConfiguration.class
})
public class OpendataCollectorServiceApplication extends SpringBootServletInitializer {

	/**
	 * 
	 */
	private static final String CONFIGURATION_DIRECTORY_NAME = "opendata-collector-service";
	
	/**
	 * 
	 */
	private static final Class<?> APPLICATION_CLASS = OpendataCollectorServiceApplication.class;
	
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
