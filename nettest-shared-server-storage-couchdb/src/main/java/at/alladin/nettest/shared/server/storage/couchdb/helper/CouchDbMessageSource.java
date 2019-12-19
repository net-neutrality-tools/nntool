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

package at.alladin.nettest.shared.server.storage.couchdb.helper;

import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.alladin.nettest.shared.server.helper.spring.CachingAndReloadingMessageSource;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Translation;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.TranslationRepository;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbMessageSource extends CachingAndReloadingMessageSource<Translation> {

	private static final Logger logger = LoggerFactory.getLogger(CouchDbMessageSource.class);
	
	private final TranslationRepository translationRepository;
	
	public CouchDbMessageSource(TranslationRepository translationRepository) {
		this.translationRepository = translationRepository;
	}
	
	////
	
	protected String getTranslation(String code, Translation translationObject) {
		if (translationObject != null && !translationObject.getTranslations().isEmpty()) {
			return translationObject.getTranslations().get(code);
		}
		
		return null;
	}
	
	protected Translation loadTranslation(final Locale locale) {
		logger.info("Loading translation for locale {} from CouchDB.", locale);
		final Optional<Translation> optionalTranslation = translationRepository.findByLanguage(locale.getLanguage());
		
		return optionalTranslation.orElse(null);
	}
}
