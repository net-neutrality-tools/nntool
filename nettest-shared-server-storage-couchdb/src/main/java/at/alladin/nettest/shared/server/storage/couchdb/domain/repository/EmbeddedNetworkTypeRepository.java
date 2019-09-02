package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import java.util.List;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.EmbeddedNetworkType;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

public interface EmbeddedNetworkTypeRepository extends CouchDbRepository<EmbeddedNetworkType> {

	EmbeddedNetworkType findByNetworkTypeId(Integer networkTypeId);
	
	List<EmbeddedNetworkType> findAll();
	
}
