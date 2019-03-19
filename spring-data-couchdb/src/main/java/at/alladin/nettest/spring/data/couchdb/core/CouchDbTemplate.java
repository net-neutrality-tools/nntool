package at.alladin.nettest.spring.data.couchdb.core;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.util.Streamable;
import org.springframework.util.StringUtils;

import com.cloudant.client.api.query.Expression;
import com.cloudant.client.api.query.QueryBuilder;

import at.alladin.nettest.couchdb.client.CouchDbDatabase;
import at.alladin.nettest.couchdb.client.CouchDbQueryResult;
import at.alladin.nettest.couchdb.client.CouchDbResponse;
import at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbTemplate implements CouchDbOperations {

	//private static final Logger logger = LoggerFactory.getLogger(CouchDbTemplate.class);
	
	private static final String PROPERTY_DOCTYPE = "docType"; // TODO: rename to @docType
	
	private final CouchDbDatabase database;
	
	/**
	 * 
	 * @param database
	 */
	public CouchDbTemplate(CouchDbDatabase database) {
		this.database = database;
	}
	
	@Override
	public String getDatabaseName(Class<?> entityClass) {
		// TODO: determine
		return null;
	}
	
	@Override
	public CouchDbDatabase getCouchDbDatabase() {
		return database;
	}
	
	//database.bulk(objects)

	@Override
	public <T, S extends T> S save(S entity, CouchDbEntityInformation<T, String> entityInformation) {
		final CouchDbResponse response = database.save(entity);
		handleCouchDbResponse(response, entityInformation, entity);
		return entity;
	}
	
//	@Override
//	public <T, S extends T> S insert(S entity, CouchDbEntityInformation<T, String> entityInformation) {
//		final CouchDbResponse response = database.save(entity);
//		handleCouchDbResponse(response, entityInformation, entity);
//		return entity;
//	}

	@Override
	public <T, S extends T> S update(S entity, CouchDbEntityInformation<T, String> entityInformation) {
		final CouchDbResponse response = database.update(entity);
		handleCouchDbResponse(response, entityInformation, entity);
		return entity;
	}
	
	@Override
	public <T, S extends T> Iterable<S> saveAll(Iterable<S> entities, CouchDbEntityInformation<T, String> entityInformation) {
		final List<S> entityList = Streamable.of(entities).stream().collect(Collectors.toList());
		return bulk(entityList, entityInformation);
	}
	
	@Override
	public <T> T findById(String id, Class<T> entityClass) {
		return database.find(entityClass, id);
	}
	
	@Override
	public boolean exists(String id) {
		return database.contains(id);
	}
	
	@Override
	public <T> Iterable<T> findAll(CouchDbEntityInformation<T, String> entityInformation) {
		final CouchDbQueryResult<T> result = database.query(new QueryBuilder(Expression.eq(PROPERTY_DOCTYPE, entityInformation.getDocType())).build(), entityInformation.getJavaType());
		// TODO
		return result.getDocs();
	}
	
	//database.query(new QueryBuilder(Expression.eq("_id", id)).build(), entityClass);
	
	@Override
	public <T> void remove(Object objectToRemove, CouchDbEntityInformation<T, String> entityInformation) {
		final CouchDbResponse response = database.remove(objectToRemove);
		handleCouchDbResponse(response, entityInformation, null);
	}
	
	@Override
	public <T> CouchDbQueryResult<T> query(String query, Class<T> entityClass) {
		return database.query(query, entityClass);
	}
	
	/////
	
	protected <T> void handleCouchDbResponse(final CouchDbResponse response, CouchDbEntityInformation<T, String> entityInformation, T nullableEntity) {
		final String error = response.getError();
		
		if (StringUtils.hasLength(error)) {
			// TODO: process error
			//throw new CouchDbDatabaseException("response contains error: " + error); // TODO: custom exception, more info: id, rev, reason
		}
		
		updateEntity(nullableEntity, entityInformation, response);
	}
	
	protected <T> void updateEntity(T nullableEntity, CouchDbEntityInformation<T, String> entityInformation, final CouchDbResponse response) {
		if (nullableEntity != null) {
			if (entityInformation.isNew(nullableEntity)) {
				entityInformation.setId(nullableEntity, response.getId());
			}
			
			entityInformation.setRev(nullableEntity, response.getRev());
		}
	}
	
	protected <T, S extends T> List<S> bulk(List<S> entityList, CouchDbEntityInformation<T, String> entityInformation) {
		final List<CouchDbResponse> responseList = database.bulk(entityList);
		
		for (int i = 0; i < responseList.size(); i++) {
			// TODO: check if sizes are equal/throw if there's a mismatch
			final CouchDbResponse response = responseList.get(i);

			final String error = response.getError();
			if (error != null) {
				// TODO: process error
			}
			
			final T entity = entityList.get(i);
			updateEntity(entity, entityInformation, response);
		}
		
		return entityList;
	}
}
