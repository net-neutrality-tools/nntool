package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;


import at.alladin.nettest.shared.server.storage.couchdb.domain.model.TaskConfigurationQoS;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.TaskConfigurationSpeed;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

public interface TaskConfigurationQoSRepository extends CouchDbRepository<TaskConfigurationQoS> {

	TaskConfigurationQoS findByNameAndVersion(String name, String version);
	
}
