package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Provider;
import at.alladin.nettest.spring.data.couchdb.core.query.Key;
import at.alladin.nettest.spring.data.couchdb.core.query.View;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author lb@alladin.at
 *
 */
public interface ProviderRepository extends CouchDbRepository<Provider> {

	List<Provider> findAll();
	
	/**
	 * 
	 * @param asn
	 * @return
	 */
	@View(
		designDocument = "Settings", viewName = "provider_by_asn", includeDocs = true, descending = true,
		startKey = {
			@Key(value = "asn", highSentinel = true),
		},
		endKey = {
			@Key(value = "asn", nullValue = true),
		}
	)
	List<Provider> findByAsn(@Param("asn") Long asn);

	
	
	@View(
		designDocument = "Settings", viewName = "provider_by_mccmnc", includeDocs = true, descending = true,
		startKey = {
			@Key(value = "mcc", highSentinel = false),
			@Key(value = "mnc", highSentinel = false),
		},
		endKey = {
			@Key(value = "mcc", nullValue = false),
			@Key(value = "mnc", nullValue = false),
		}
	)
	List<Provider> findBySimMccMnc(@Param("mcc") Integer mcc, @Param("mnc") Integer mnc);
}
