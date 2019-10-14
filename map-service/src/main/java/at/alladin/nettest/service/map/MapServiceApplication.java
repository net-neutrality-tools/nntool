package at.alladin.nettest.service.map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;

import at.alladin.nettest.service.map.config.MapCacheConfig;
import at.alladin.nettest.service.map.config.MapServiceSettingsConfig;
import at.alladin.nettest.shared.server.helper.spring.SpringApplicationHelper;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@SpringBootApplication
@EnableConfigurationProperties({
	MapCacheConfig.class,
	MapServiceSettingsConfig.class
})

@ComponentScan({"at.alladin.nettest.shared.server.service", "at.alladin.nettest.service.map.service", "at.alladin.nettest.service.map"})
public class MapServiceApplication extends SpringBootServletInitializer {

	/**
	 * 
	 */
	private static final String CONFIGURATION_DIRECTORY_NAME = "map-service";
	
	/**
	 * 
	 */
	private static final Class<?> APPLICATION_CLASS = MapServiceApplication.class;
	
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

	/*
	@Bean
	public MessageSource messageSource() {
		final ResourceBundleMessageSource messageSource = new SqlTranslationMessageSource();
		messageSource.setBasename("language");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}
	*/
}
