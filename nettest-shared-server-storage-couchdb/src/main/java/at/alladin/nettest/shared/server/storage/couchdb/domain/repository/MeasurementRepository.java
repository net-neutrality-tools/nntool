package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.spring.data.couchdb.core.query.Key;
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
	
	List<Measurement> findByAgentInfoUuid(String uuid);
	
	@View(
		designDocument = "Measurement", viewName = "by_agent_uuid", includeDocs = true, descending = true,
		startKey = {
			@Key(value = "agentUuid", highSentinel = true),
			//@Key("agentUuid"),
			//@Key(highSentinel = true)
		},
		endKey = {
			@Key(value = "agentUuid", nullValue = true),
			//@Key("agentUuid"),
			//@Key(nullValue = true)
		}
	)
	Page<Measurement> findByAgentInfoUuidOrderByMeasurementTimeStartTimeDesc(@Param("agentUuid") String agentUuid, Pageable pageable);
	
	////
	
	@View(
		designDocument = "Measurement", viewName = "by_agent_uuid", includeDocs = true, descending = true,
		startKey = {
			@Key(highSentinel = true)
		},
		endKey = {
			@Key(nullValue = true)
		}
	)
	Page<Measurement> findAllTest123(Pageable pageable);
	
// spring-data-couchdb Demo:
	
//	@MangoQuery("{\n" + 
//	"   \"selector\": {\n" + 
//	"      \"docType\": \"?docType\",\n" + 
//	"      \"uuid\": \"?0\"\n" + 
//	"   }\n" + 
//	"}")
//	Measurement findByUuidWithMangoQueryAnnotation(String uuid);
	
//	@MangoQuery("{\n" + 
//	"   \"selector\": {\n" + 
//	"      \"docType\": \"?docType\"\n" +
//	"   }\n" + 
//	"}")
//	List<Measurement> findAllMangoQuery();
	
//	@View(designDocument = "Test", viewName = "byUuid", limit = 1, includeDocs = true)
//	Measurement findByViewSingle(@Key String uuid);
	
//	@View(designDocument = "Test", viewName = "byUuid", limit = 1, includeDocs = true)
//	Optional<Measurement> findByViewSingleOptional(@Key String uuid);
	
//	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
//	Measurement[] findByViewArray(@Key String uuid);
	
//	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
//	List<Measurement> findByViewList(@Key String uuid);
	
//	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
//	Stream<Measurement> findByViewStream(@Key String uuid);
}
