package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Device;
import at.alladin.nettest.spring.data.couchdb.core.query.View;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;
import at.alladin.nettest.spring.data.couchdb.core.query.Key;

/**
 * 
 * @author lb@alladin.at
 *
 */
public interface DeviceRepository extends CouchDbRepository<Device> {

	@View(
		designDocument = "Settings", viewName = "by_device", includeDocs = true, descending = true
	)
	Page<Device> getAllDevices(Pageable page);
	
	@View(
	    designDocument = "Settings", viewName = "by_device", includeDocs = true, descending = true,
	    keys = {
	        @Key(value = "codeName")
	    }
	)
	Device findByCodeName(@Param("codeName") String codeName);
}
