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
		final ParametersParameterAccessor paramAccessor = new ParametersParameterAccessor(method.getParameters(), parameters);
		
		final MangoQueryCreator creator = new MangoQueryCreator(
			partTree, 
			paramAccessor, 
			method.getDocType()
		);
		
		final String query = creator.createQuery();
		
		logger.debug("PartTree query: {}", query);
		
		final CouchDbQueryResult<?> queryResult = operations.query(query, getQueryMethod().getReturnedObjectType());
		
		final List<?> docs = queryResult.getDocs();
		
		if (method.isCollectionQuery()) {
			return docs;
		}
		
		if (method.isStreamQuery()) {
			return docs.stream();
		}
		
		if (docs.size() > 0) {
			return docs.get(0);
		}
		
		return null;
	}
}
