package at.alladin.nettest.shared.server.config.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.alladin.nettest.shared.server.helper.spring.CodeInsteadOfExceptionMessageSource;
import at.alladin.nettest.shared.server.service.storage.v1.StorageTranslationService;

/**
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public class MessageSourceConfiguration {

	@Autowired
	private StorageTranslationService storageTranslationService;
	
	@Bean
	public MessageSource messageSource() {
		return new CodeInsteadOfExceptionMessageSource(storageTranslationService.getDefaultMessageSource());
	}
}
