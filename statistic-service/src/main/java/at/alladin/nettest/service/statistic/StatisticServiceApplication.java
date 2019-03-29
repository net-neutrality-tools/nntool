package at.alladin.nettest.service.statistic;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import at.alladin.nettest.service.statistic.config.DataExportConfiguration;
import at.alladin.nettest.shared.server.helper.spring.SpringApplicationHelper;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@SpringBootApplication
@EnableConfigurationProperties(DataExportConfiguration.class)
@ComponentScan({
	"at.alladin.nettest.service.statistic",
	"at.alladin.nettest.shared.server.web.api.v1"
})
public class StatisticServiceApplication extends SpringBootServletInitializer {

	/**
	 * 
	 */
	private static final String CONFIGURATION_DIRECTORY_NAME = "statistic-service";
	
	/**
	 * 
	 */
	private static final Class<?> APPLICATION_CLASS = StatisticServiceApplication.class;
	
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
