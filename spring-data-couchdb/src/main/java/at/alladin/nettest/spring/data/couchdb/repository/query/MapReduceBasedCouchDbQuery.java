package at.alladin.nettest.spring.data.couchdb.repository.query;

import java.io.IOException;
import java.util.List;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.UnpaginatedRequestBuilder;
import com.cloudant.client.api.views.ViewRequest;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.cloudant.client.api.views.ViewResponse;

import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;
import at.alladin.nettest.spring.data.couchdb.core.query.View;

public class MapReduceBasedCouchDbQuery extends AbstractCouchDbRepositoryQuery {

	//private static final Logger logger = LoggerFactory.getLogger(MapReduceBasedCouchDbQuery.class);
	
	public MapReduceBasedCouchDbQuery(CouchDbQueryMethod method, CouchDbOperations operations) {
		super(method, operations);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.RepositoryQuery#execute(java.lang.Object[])
	 */
	@Override
	public Object execute(Object[] parameters) {
		
		final View view = method.getViewAnnotation();
		if (view == null) {
			return null;
		}
		
		// TODO: implement keys/startKey/endKey with value annotations
		//final ParametersParameterAccessor accessor = new ParametersParameterAccessor(method.getParameters(), parameters);
		
		final Class<?> entityType = method.getReturnedObjectType(); //method.getEntityInformation().getJavaType();
		
		// TODO: for pagination: query count and set limit and skip accordingly
		//final Pageable pageable = accessor.getPageable();
		
		final Database impl = (Database) operations.getCouchDbDatabase().getImpl();
		
		final ViewRequest<?, ?> viewRequest = buildViewRequest(impl, view, entityType);
		final ViewResponse<?, ?> viewResponse;
		
		try {
			viewResponse = viewRequest.getResponse();
		} catch (IOException e) { // TODO
			return null; // TODO: throw exception
		}
		
		// TODO: check viewResponse (entity count, null checks, etc.)
		
		List<?> docs = viewResponse.getDocsAs(entityType);
		
		if (method.isCollectionQuery()) {
			return docs;
		}
		
		if (method.isStreamQuery()) {
			return docs.stream();
		}
		
		return docs.get(0); // TODO: check if there's at least one element
	}
	
	private ViewRequest<?, ?> buildViewRequest(Database impl, View view, Class<?> entityType) {
		// TODO: guess designDocument and viewName if not present
		ViewRequestBuilder builder = impl.getViewRequestBuilder(view.designDocument(), view.viewName());
		UnpaginatedRequestBuilder<String, ?> unpaginatedBuilder = builder.newRequest(Key.Type.STRING, entityType);

		unpaginatedBuilder.reduce(view.reduce());
		unpaginatedBuilder.includeDocs(view.includeDocs());
		unpaginatedBuilder.descending(view.descending());
		unpaginatedBuilder.group(view.group());
		
//		if (view.keys().length > 0) {
//			unpaginatedBuilder.keys(view.keys());
//		}
//		if (StringUtils.hasLength(view.startKey())) {
//			unpaginatedBuilder.startKey(view.startKey());
//		}
//		if (StringUtils.hasLength(view.endKey())) {
//			unpaginatedBuilder.endKey(view.endKey());
//		}
		if (view.limit() > 0) {
			unpaginatedBuilder.limit(view.limit());
		}
		if (view.skip() > 0) {
			unpaginatedBuilder.skip(view.skip());
		}
		
		return unpaginatedBuilder.build();
	}
}
