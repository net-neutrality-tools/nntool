/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.shared.server.helper.spring;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.util.StringUtils;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public abstract class CachingAndReloadingMessageSource<T> extends AbstractMessageSource {

	private static final Logger logger = LoggerFactory.getLogger(CachingAndReloadingMessageSource.class);
	
	private static final long RELOAD_AFTER_SECONDS = 60;
	private static final Locale FALLBACK_LOCALE = Locale.ENGLISH;
	
	protected final Map<Locale, TranslationHolder<T>> translationMap = new ConcurrentHashMap<>();
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		return createMessageFormat(translate(code, locale), locale);
	}
	
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		return translate(code, locale);
	}
	
	////
	
	protected abstract String getTranslation(String code, T translationObject);
	protected abstract T loadTranslation(Locale locale);
	
	////
	
	private String translate(String code, Locale locale) {
		load(locale);
		
		T translationObject = translationMap.get(locale).translation;
		String translation;
		if (translationObject != null) {
			translation = getTranslation(code, translationObject);
			if (StringUtils.hasLength(translation)) {
				return translation;
			}
		}
		
		// fallback to English
		logger.debug("Translation of code '{}' not found for locale '{}', falling back to English", code, locale);
		
		load(FALLBACK_LOCALE);
		translationObject = translationMap.get(FALLBACK_LOCALE).translation;
		translation = getTranslation(code, translationObject);
		
		return translation;
	}
	
	private void load(final Locale locale) {
		if (shouldSkipTranslationLoading(locale)) {
			return;
		}
		
		logger.info("Loading translation for locale {}.", locale);
		final T translation = loadTranslation(locale);
		
		if (translation != null) {
			translationMap.put(locale, new TranslationHolder<T>(translation));
			logger.info("Successfully loaded translation for locale {}.", locale);
		} else {
			logger.info("Could not load translation for locale {}, inserting dummy.", locale);
			
			// Translation for the specified locale could not be found.
			// Insert a dummy translation into the map to prevent a lot of database queries for the same (non-existent) translation.
			// The translation will be reloaded after some time.
			translationMap.put(locale, new TranslationHolder<T>(null));
		}
	}
	
	private boolean shouldSkipTranslationLoading(final Locale locale) {
		final TranslationHolder<T> holder = translationMap.get(locale);
		
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
	
	protected static class TranslationHolder<T> {
		T translation;
		long lastUpdated;
		
		TranslationHolder(T translation) {
			this.translation = translation;
			this.lastUpdated = new Date().getTime();
		}
		
		/*TranslationHolder(T translation, long lastUpdated) {
			this.translation = translation;
			this.lastUpdated = lastUpdated;
		}*/
	}
}
