package at.alladin.nettest.shared.server.storage.couchdb.helper;

import java.text.MessageFormat;
import java.util.Date;
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
	
	private static final long RELOAD_AFTER_SECONDS = 60;
	
	private final Map<Locale, TranslationHolder> translationMap = new ConcurrentHashMap<>();
	
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
		
		Translation translation = translationMap.get(locale).translation;
		if (translation != null && !translation.getTranslations().isEmpty()) {
			return translation.getTranslations().get(code);
		}
		
		// fallback to English
		logger.debug("Translation of code '{}' not found for locale '{}', falling back to English", code, locale);
		
		loadTranslation(Locale.ENGLISH);
		translation = translationMap.get(Locale.ENGLISH).translation;
		if (translation == null) {
			logger.debug("English translation not available.");
			return null;
		}
		
		return translation.getTranslations().get(code);
	}
	
	private void loadTranslation(final Locale locale) {
		if (shouldSkipTranslationLoading(locale)) {
			return;
		}
		
		logger.info("Loading translation for locale {} from CouchDB.", locale);
		final Optional<Translation> optionalTranslation = translationRepository.findByLanguage(locale.getLanguage());
		
		if (optionalTranslation.isPresent()) {
			final Translation translation = optionalTranslation.get();
			
			translationMap.put(locale, new TranslationHolder(translation));
			logger.info("Successfully loaded translation (count: {}) for locale {} from CouchDB.", translation.getTranslations().size(), locale);
		} else {
			logger.info("Could not load translation for locale {} from CouchDB, inserting dummy.", locale);
			
			// Translation for the specified locale could not be found.
			// Insert a dummy translation into the map to prevent a lot of database queries for the same (non-existent) translation.
			// The translation will be reloaded after some time.
			final Translation dummyTranslation = new Translation();
			dummyTranslation.setLanguage(locale.getLanguage());

			translationMap.put(locale, new TranslationHolder(dummyTranslation));
		}
	}
	
	private boolean shouldSkipTranslationLoading(final Locale locale) {
		final TranslationHolder holder = translationMap.get(locale);
		
		// Don't skip if we don't have a translation.
		if (holder == null) {
			return false;
		}
		
		// Don't skip if the translation needs to be reloaded.
		if (new Date().after(new Date(holder.lastUpdated + RELOAD_AFTER_SECONDS * 1000))) {
			logger.debug("Reloading translation {} (translation is older than {} seconds)", locale, RELOAD_AFTER_SECONDS);
			return false;
		}
			
		return true;
	}
	
	private static class TranslationHolder {
		Translation translation;
		long lastUpdated;
		
		TranslationHolder(Translation translation) {
			this.translation = translation;
			this.lastUpdated = new Date().getTime();
		}
		
		/*TranslationHolder(Translation translation, long lastUpdated) {
			this.translation = translation;
			this.lastUpdated = lastUpdated;
		}*/
	}
}
