package at.alladin.nettest.spring.data.couchdb.repository;

import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public interface CouchDbRepository<T> extends CrudRepository<T, String> {

}
