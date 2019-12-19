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

package at.alladin.nettest.spring.data.couchdb.repository.query;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.parser.PartTree;

import at.alladin.nettest.couchdb.client.CouchDbQueryResult;
import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class PartTreeMangoBasedCouchDbQuery extends AbstractCouchDbRepositoryQuery {

	private final Logger logger = LoggerFactory.getLogger(PartTreeMangoBasedCouchDbQuery.class);

	private final PartTree partTree;
	
	
	public PartTreeMangoBasedCouchDbQuery(CouchDbQueryMethod method, CouchDbOperations operations) {
		super(method, operations);
		
		partTree = new PartTree(method.getName(), method.getResultProcessor().getReturnedType().getDomainType());
	}

	@Override
	public Object execute(Object[] parameters) {
		// Mango queries don't support aggregate functions (see https://github.com/apache/couchdb/issues/1254) so it's not 
		// possible to execute pagination queries.
		if (method.isPageQuery()) {
			throw new IllegalArgumentException("Pageination queries are currently not possible with Mango queries.");
		}
		
		final ParametersParameterAccessor accessor = new ParametersParameterAccessor(method.getParameters(), parameters);

		final MangoQueryCreator creator = new MangoQueryCreator(
			partTree, 
			accessor, 
			method.getDocType(),
			method.getResultProcessor().getReturnedType().getDomainType()
		);
		
		final String query = creator.createQuery();
		
		logger.debug("PartTree query: {}", query);
		
		final CouchDbQueryResult<?> queryResult = operations.query(query, getQueryMethod().getReturnedObjectType());
		
		final List<?> docs = queryResult.getDocs();
		
		if (method.isCollectionQuery()) {
			return docs;
		} else if (method.isStreamQuery()) {
			return docs.stream();
		} /*else if (method.isPageQuery()) {
			return new PageImpl<>(docs, accessor.getPageable(), ...);
		}*/ else if (docs.size() > 0) {
			return docs.get(0);
		}
		
		return null;
	}
}
