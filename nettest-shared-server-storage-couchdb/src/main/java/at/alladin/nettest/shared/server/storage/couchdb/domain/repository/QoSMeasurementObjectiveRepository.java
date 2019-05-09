package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;


import java.util.List;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

public interface QoSMeasurementObjectiveRepository extends CouchDbRepository<QoSMeasurementObjective> {

	List<QoSMeasurementObjective> findAllByEnabled(boolean enabled);
	
}
