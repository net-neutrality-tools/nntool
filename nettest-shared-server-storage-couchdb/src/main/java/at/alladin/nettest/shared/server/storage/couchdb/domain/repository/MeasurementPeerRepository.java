package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import java.util.List;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServer;
import at.alladin.nettest.spring.data.couchdb.core.query.MangoQuery;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

public interface MeasurementPeerRepository extends CouchDbRepository<MeasurementServer> {

	MeasurementServer findByUuid(String uuid);
	
	MeasurementServer findByPublicIdentifier(String publicIdentifier);
	
	@MangoQuery("{\n" + 
			"   \"selector\": {\n" + 
			"      \"docType\": \"MeasurementServer\",\n" + 
			"      \"type\": \"SPEED\",\n" + 
			"      \"enabled\": true\n" + 
			"   }\n" + 
			"}")
	List<MeasurementServer> getAvailableSpeedMeasurementPeers();
	
}
