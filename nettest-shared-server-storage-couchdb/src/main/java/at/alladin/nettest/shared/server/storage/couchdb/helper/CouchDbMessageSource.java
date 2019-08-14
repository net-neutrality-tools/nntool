package at.alladin.nettest.shared.server.storage.couchdb.helper;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractMessageSource;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Translation;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.TranslationRepository;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbMessageSource extends AbstractMessageSource {

	private static final Logger logger = LoggerFactory.getLogger(CouchDbMessageSource.class);
	
	private final Map<Locale, Translation> translationMap = new ConcurrentHashMap<>();
	
	// TODO: reload every once in a while
	
	private final TranslationRepository translationRepository;
	
	public CouchDbMessageSource(TranslationRepository translationRepository) {
		this.translationRepository = translationRepository;
	}
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		return createMessageFormat(getTranslation(code, locale), locale);
	}
	
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		return getTranslation(code, locale);
	}
	
	////
	
	private String getTranslation(String code, Locale locale) {
		loadTranslation(locale);
		
		final Translation translation = translationMap.get(locale);
		if (translation == null) {
			return null;
		}
		
		return translation.getTranslations().get(code);
	}
	
	private void loadTranslation(final Locale locale) {
		if (translationMap.containsKey(locale)) {
			return;
		}
		
		logger.info("Loading translation for locale {} from CouchDB.", locale);
		final Optional<Translation> optionalTranslation = translationRepository.findByLanguage(locale.getLanguage());
		
		if (optionalTranslation.isPresent()) {
			final Translation translation = optionalTranslation.get();
			
			translationMap.put(locale, translation);
			logger.info("Successfully loaded translation (count: {}) for locale {} from CouchDB.", translation.getTranslations().size(), locale);
		} else {
			// TODO: insert dummy translation to prevent database query every time this locale is used.
			logger.info("Could not load translation for locale {} from CouchDB.", locale);
		}
	}
}
