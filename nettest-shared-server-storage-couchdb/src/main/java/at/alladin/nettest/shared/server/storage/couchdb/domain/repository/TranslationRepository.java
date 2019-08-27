package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import java.util.Optional;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Translation;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface TranslationRepository extends CouchDbRepository<Translation> {

	/**
	 * 
	 * @param language
	 * @return
	 */
	Optional<Translation> findByLanguage(String language);
}
