package at.alladin.nettest.spring.data.couchdb.repository.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbPagingAndSortingRepository;
import at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public class PagingAndSortingCouchDbRepository<T> extends SimpleCouchDbRepository<T> implements CouchDbPagingAndSortingRepository<T> {

	public PagingAndSortingCouchDbRepository(CouchDbEntityInformation<T, String> entityInformation, CouchDbOperations couchDbOperations) {
		super(entityInformation, couchDbOperations);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
	 */
	@Override
	public Iterable<T> findAll(Sort sort) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("findAll(Sort sort) has not yet been implemented.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<T> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("findAll(Pageable pageable) has not yet been implemented.");
	}
}
