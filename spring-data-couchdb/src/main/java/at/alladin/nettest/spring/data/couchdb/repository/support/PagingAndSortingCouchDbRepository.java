/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
