package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.server.service.storage.v1.StorageTranslationService;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.TranslationRepository;
import at.alladin.nettest.shared.server.storage.couchdb.helper.CouchDbMessageSource;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class CouchDbStorageTranslationService implements StorageTranslationService {

	@Autowired
	private TranslationRepository translationRepository;
	
	@Override
	public MessageSource getDefaultMessageSource() {
		return new CouchDbMessageSource(translationRepository);
	}
}
