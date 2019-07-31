package at.alladin.nettest.spring.data.couchdb.repository.query;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.util.StringUtils;

import at.alladin.nettest.couchdb.client.CouchDbQueryResult;
import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;
import at.alladin.nettest.spring.data.couchdb.core.query.MangoQuery;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class MangoBasedCouchDbQuery extends AbstractCouchDbRepositoryQuery {

	private static final Logger logger = LoggerFactory.getLogger(MangoBasedCouchDbQuery.class);
	
	private final String query;
	
	public MangoBasedCouchDbQuery(CouchDbQueryMethod method, CouchDbOperations operations) {
		super(method, operations);
		
		query = Optional.ofNullable(method.getMangoQueryAnnotation())
						.map(MangoQuery::value)
						.orElse(null);
	}

	@Override
	public Object execute(Object[] parameters) {
		//final EvaluationContext evaluationContext = ?
		
		// TODO: improve this method, add error handling/validation etc.
		
		// Mango queries don't support aggregate functions (see https://github.com/apache/couchdb/issues/1254) so it's not 
		// possible to execute pagination queries.
		if (method.isPageQuery()) {
			throw new IllegalArgumentException("Pageination queries are currently not possible with Mango queries.");
		}
		
		if (StringUtils.isEmpty(query)) { // TODO: this should be validated
			return null;
		}
		
		logger.debug("query template: {}", query);
		
		final String docType = method.getDocType();

		String currentQuery = new String(query);
		
		currentQuery = currentQuery.replace("?docType", docType);
		
		final ParametersParameterAccessor accessor = new ParametersParameterAccessor(method.getParameters(), parameters);
		
		for (int i = 0; i < accessor.getParameters().getNumberOfParameters(); i++) {
			final Parameter p = accessor.getParameters().getBindableParameter(i);
			currentQuery = currentQuery.replace(p.getPlaceholder(), accessor.getBindableValue(p.getIndex()).toString());
		}

		logger.debug("actual query: {}", currentQuery);
		
		final CouchDbQueryResult<?> queryResult = operations.query(currentQuery, getQueryMethod().getEntityInformation().getJavaType());
		
		List<?> docs = queryResult.getDocs();
		
		if (method.isCollectionQuery()) {
			return docs;
		} else if (method.isStreamQuery()) {
			return docs.stream();
		}/* else if (method.isPageQuery()) {
			return new PageImpl<>(docs, accessor.getPageable(), ...);
		}*/ else if (docs.size() > 0) {
			return docs.get(0);
		}
		
		return null;
	}
}
