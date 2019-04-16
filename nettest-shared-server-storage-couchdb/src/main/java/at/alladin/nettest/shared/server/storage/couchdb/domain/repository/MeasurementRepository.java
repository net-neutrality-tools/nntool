package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.spring.data.couchdb.core.query.Key;
import at.alladin.nettest.spring.data.couchdb.core.query.MangoQuery;
import at.alladin.nettest.spring.data.couchdb.core.query.View;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface MeasurementRepository extends CouchDbRepository<Measurement> {

	Measurement findByUuid(String uuid);

	Measurement findByUuidAndUuidExists(String uuid);
	
	List<Measurement> findByAgentInfoUuid/*OrderByStarttimeDesc*/(String uuid, Pageable pageable);
	
	////
	
	@MangoQuery("{\n" + 
	"   \"selector\": {\n" + 
	"      \"docType\": \"?docType\",\n" + 
	"      \"uuid\": \"?0\"\n" + 
	"   }\n" + 
	"}")
	Measurement findByUuidWithMangoQueryAnnotation(String uuid);
	
	@MangoQuery("{\n" + 
	"   \"selector\": {\n" + 
	"      \"docType\": \"?docType\"\n" +
	"   }\n" + 
	"}")
	List<Measurement> findAllMangoQuery();
	
	@View(designDocument = "Test", viewName = "byUuid", limit = 1, includeDocs = true)
	Measurement findByViewSingle(@Key String uuid);
	
	@View(designDocument = "Test", viewName = "byUuid", limit = 1, includeDocs = true)
	Optional<Measurement> findByViewSingleOptional(@Key String uuid);
	
//	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
//	Measurement[] findByViewArray(@Key String uuid);
	
	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
	List<Measurement> findByViewList(@Key String uuid);
	
	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
	Stream<Measurement> findByViewStream(@Key String uuid);
}
