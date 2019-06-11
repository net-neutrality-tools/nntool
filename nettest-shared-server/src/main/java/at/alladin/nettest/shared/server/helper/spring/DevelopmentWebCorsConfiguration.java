package at.alladin.nettest.shared.server.helper.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Profile("dev")
@Configuration
public class DevelopmentWebCorsConfiguration implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	    registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
	}
}
