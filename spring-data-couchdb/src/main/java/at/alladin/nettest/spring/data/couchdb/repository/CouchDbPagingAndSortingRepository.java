package at.alladin.nettest.spring.data.couchdb.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public interface CouchDbPagingAndSortingRepository<T> extends CouchDbRepository<T>, PagingAndSortingRepository<T, String> {
	
}
