package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;


import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServer;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

public interface MeasurementServerRepository extends CouchDbRepository<MeasurementServer> {

	MeasurementServer findByUuid(String uuid);
	
}
