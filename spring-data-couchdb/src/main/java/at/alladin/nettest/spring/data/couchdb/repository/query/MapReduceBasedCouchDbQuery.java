package at.alladin.nettest.spring.data.couchdb.repository.query;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.util.StringUtils;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.Key.ComplexKey;
import com.cloudant.client.api.views.UnpaginatedRequestBuilder;
import com.cloudant.client.api.views.ViewRequest;
import com.cloudant.client.api.views.ViewResponse;

import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;
import at.alladin.nettest.spring.data.couchdb.core.query.Key;
import at.alladin.nettest.spring.data.couchdb.core.query.View;

public class MapReduceBasedCouchDbQuery extends AbstractCouchDbRepositoryQuery {

	private static final Logger logger = LoggerFactory.getLogger(MapReduceBasedCouchDbQuery.class);
	
	private ComplexKey keys;
	private ComplexKey startKey;
	private ComplexKey endKey;
	
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
		final ParametersParameterAccessor accessor = new ParametersParameterAccessor(method.getParameters(), parameters);
		final Pageable pageable = accessor.getPageable();
		
		final Database impl = (Database) operations.getCouchDbDatabase().getImpl();
		
		// Determine keys, startKey and endKey
		keys = parseKeyArray(view.keys(), accessor);
		logger.debug("keys: {}", keys != null ? keys.toJson() : null);
		
		startKey = parseKeyArray(view.startKey(), accessor);
		logger.debug("startKey: {}", startKey != null ? startKey.toJson() : null);
		
		endKey = parseKeyArray(view.endKey(), accessor);
		logger.debug("endKey: {}", endKey != null ? endKey.toJson() : null);

		// Execute count query if it's a paginated query.
		long total = 0;
		if (method.isPageQuery()) {
			final ViewRequest<?, ?> viewRequest = buildCountViewRequest(impl, view, accessor);
			final ViewResponse<?, ?> viewResponse;
			
			try {
				viewResponse = viewRequest.getResponse();
				total = Long.parseUnsignedLong(String.valueOf(viewResponse.getValues().get(0)));
				
				logger.debug("Count response: {}", total);
			} catch (Exception e) {
				logger.error("Could not get total count, returning empty result"/*, e*/);
				//e.printStackTrace();
				return returnEmptyResult(accessor, total); // TODO: throw exception
			}
		}
		
		final Class<?> entityType = method.getReturnedObjectType();
		
		final ViewRequest<?, ?> viewRequest = buildViewRequest(impl, view, pageable, entityType, accessor);
		final ViewResponse<?, ?> viewResponse;
		
		try {
			viewResponse = viewRequest.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return returnEmptyResult(accessor, total); // TODO: throw exception
		}
		
		// TODO: check viewResponse (entity count, null checks, etc.)
		
		final List<?> docs = viewResponse.getDocsAs(entityType);
		
		if (method.isCollectionQuery()) {
			return docs;
		} else if (method.isStreamQuery()) {
			return docs.stream();
		} else if (method.isPageQuery()) {
			return new PageImpl<>(docs, accessor.getPageable(), total);
		} else if (docs.size() > 0) {
			return docs.get(0);
		}
	
		return null;
	}
	
	private Object returnEmptyResult(ParametersParameterAccessor accessor, long total) {
		final List<?> emptyList = Collections.emptyList();
		
		if (method.isCollectionQuery()) {
			return emptyList;
		} else if (method.isStreamQuery()) {
			return emptyList.stream();
		} else if (method.isPageQuery()) {
			return new PageImpl<>(emptyList, accessor.getPageable(), total);
		}
	
		return null;
	}
	
	private ViewRequest<ComplexKey, ?> buildViewRequest(Database impl, View view, Pageable pageable, Class<?> entityType, ParametersParameterAccessor accessor) {
		final UnpaginatedRequestBuilder<ComplexKey, ?> builder = getBasicRequestBuilder(impl, view, entityType, accessor);

		builder.reduce(view.reduce());
		builder.includeDocs(view.includeDocs());

		if (view.limit() > 0) {
			builder.limit(view.limit());
		}
		
		if (view.skip() > 0) {
			builder.skip(view.skip());
		}
		
		if (pageable != null) {
			builder.limit(pageable.getPageSize());
			builder.skip(pageable.getOffset());
		}

		return builder.build();
	}
	
	private ViewRequest<ComplexKey, ?> buildCountViewRequest(Database impl, View view, ParametersParameterAccessor accessor) {
		final UnpaginatedRequestBuilder<ComplexKey, ?> builder = getBasicRequestBuilder(impl, view, Long.class, accessor);

		builder.reduce(true);
		builder.includeDocs(false);
		
		return builder.build();
	}
	
	private UnpaginatedRequestBuilder<ComplexKey, ?> getBasicRequestBuilder(Database impl, View view, Class<?> entityType, ParametersParameterAccessor accessor) {
		final UnpaginatedRequestBuilder<ComplexKey, ?> builder = createRequestBuilder(impl, view, entityType);

		builder.descending(view.descending());
		builder.group(view.group());
		
		if (keys != null) {
			builder.keys(keys);
		}
		
		if (startKey != null) {
			builder.startKey(startKey);
		}
		
		if (endKey != null) {
			builder.endKey(endKey);
		}

		return builder;
	}
	
	private ComplexKey parseKeyArray(Key[] keys, ParametersParameterAccessor accessor) {
		ComplexKey key = null;
		
		if (keys.length > 0) {
			final Parameters<?, ?> parameters = accessor.getParameters();
			
			for (Key k : keys) {
				if (StringUtils.hasLength(k.value())) {
					for (int i = 0; i < parameters.getBindableParameters().getNumberOfParameters(); i++) {
						final Parameter p = parameters.getBindableParameter(i);
					
						final String placeholder = p.getName().orElse(p.getPlaceholder());
						
						if (k.value().equals(placeholder)) {
							final String value = accessor.getBindableValue(p.getIndex()).toString();
							
							if (key == null) {
								key = com.cloudant.client.api.views.Key.complex(value);
							} else {
								key.add(value);
							}
							
							break; // break inner loop
						}
					}
				}
				
				if (k.nullValue()) {
					if (key == null) {
						key = com.cloudant.client.api.views.Key.complex((String)null);
					} else {
						key.add((String)null);
					}
				}
				
				if (k.highSentinel() && key != null) {
					key.addHighSentinel();
				}
			}
		}

		return key;
	}
	
	private UnpaginatedRequestBuilder<ComplexKey, ?> createRequestBuilder(Database impl, View view, Class<?> entityType) {
		String designDocument = view.designDocument();
		if (StringUtils.isEmpty(designDocument)) {
			// try to derive design document name from entity if not set in View annotation
			designDocument = entityType.getSimpleName();
		}
		
		// TODO: allow to set keyType in View annotation.
		//Key.Type<?> keyType = view.keys().length > 1 ? Key.Type.COMPLEX : Key.Type.STRING;
		
		return impl
				.getViewRequestBuilder(designDocument, view.viewName())
				.newRequest(com.cloudant.client.api.views.Key.Type.COMPLEX, entityType);
	}
}
