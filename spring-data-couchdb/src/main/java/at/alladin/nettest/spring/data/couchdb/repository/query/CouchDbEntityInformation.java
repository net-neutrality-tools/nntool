package at.alladin.nettest.spring.data.couchdb.repository.query;

import org.springframework.data.repository.core.EntityInformation;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public interface CouchDbEntityInformation<T, ID> extends EntityInformation<T, ID> {

	void setId(T entity, ID id);
	
	default boolean hasRev(T entity) {
		return getRev(entity) != null;
	}

	String getRev(T entity);
	void setRev(T entity, String rev);
	
	String getDocType();
}
