package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;


import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

public interface MeasurementAgentRepository extends CouchDbRepository<MeasurementAgent> {

	MeasurementAgent findByUuid(String uuid);

	MeasurementAgent findByUuidAndUuidExists(String uuid);
	
}
