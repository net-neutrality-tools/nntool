package at.alladin.nettest.shared.server.service.storage.v1;

import org.springframework.context.MessageSource;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface StorageTranslationService {

	/**
	 * 
	 * @return
	 */
	MessageSource getDefaultMessageSource();
}
