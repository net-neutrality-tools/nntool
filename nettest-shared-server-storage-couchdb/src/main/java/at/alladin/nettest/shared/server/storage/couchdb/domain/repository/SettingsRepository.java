package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface SettingsRepository extends CouchDbRepository<Settings> {

	Settings findByUuid(String uuid);

}
