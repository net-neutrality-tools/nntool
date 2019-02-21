package at.alladin.nettest.service.collector.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import at.alladin.nettest.service.collector.domain.model.CouchDbMeasurement;
import at.alladin.nettest.spring.data.couchdb.core.query.Key;
import at.alladin.nettest.spring.data.couchdb.core.query.MangoQuery;
import at.alladin.nettest.spring.data.couchdb.core.query.View;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface MeasurementRepository extends CouchDbRepository</*Measurement*/CouchDbMeasurement> {

	CouchDbMeasurement findByUuid(String uuid);

	CouchDbMeasurement findByUuidAndUuidExists(String uuid);
	
	@MangoQuery("{\n" + 
	"   \"selector\": {\n" + 
	"      \"docType\": \"?docType\",\n" + 
	"      \"uuid\": \"?0\"\n" + 
	"   }\n" + 
	"}")
	CouchDbMeasurement findByUuidWithMangoQueryAnnotation(String uuid);
	
	@MangoQuery("{\n" + 
	"   \"selector\": {\n" + 
	"      \"docType\": \"?docType\"\n" +
	"   }\n" + 
	"}")
	List<CouchDbMeasurement> findAllMangoQuery();
	
	@View(designDocument = "Test", viewName = "byUuid", limit = 1, includeDocs = true)
	CouchDbMeasurement findByViewSingle(@Key String uuid);
	
	@View(designDocument = "Test", viewName = "byUuid", limit = 1, includeDocs = true)
	Optional<CouchDbMeasurement> findByViewSingleOptional(@Key String uuid);
	
//	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
//	CouchDbMeasurement[] findByViewArray(@Key String uuid);
	
	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
	List<CouchDbMeasurement> findByViewList(@Key String uuid);
	
	@View(designDocument = "Test", viewName = "byUuid", includeDocs = true)
	Stream<CouchDbMeasurement> findByViewStream(@Key String uuid);
}
