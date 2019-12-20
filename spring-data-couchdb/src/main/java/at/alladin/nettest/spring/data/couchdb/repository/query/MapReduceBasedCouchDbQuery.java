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

import java.util.ArrayList;
import java.util.Arrays;
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
				logger.error("Could not get total count, returning empty result", e);
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
			logger.error("error getting view response!!!", e);
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
	
	private ViewRequest<?, ?> buildViewRequest(Database impl, View view, Pageable pageable, Class<?> entityType, ParametersParameterAccessor accessor) {
		final UnpaginatedRequestBuilder<?, ?> builder = getBasicRequestBuilder(impl, view, entityType, accessor);

		builder.reduce(view.reduce());
		builder.includeDocs(view.includeDocs());

		if (view.limit() > 0) {
			builder.limit(view.limit());
		}
		
		if (view.skip() > 0) {
			builder.skip(view.skip());
		}
		
		if (pageable != null && pageable.isPaged()) {
			builder.limit(pageable.getPageSize());
			builder.skip(pageable.getOffset());
		}

		return builder.build();
	}
	
	private ViewRequest<?, ?> buildCountViewRequest(Database impl, View view, ParametersParameterAccessor accessor) {
		final UnpaginatedRequestBuilder<?, ?> builder = getBasicRequestBuilder(impl, view, Long.class, accessor);

		builder.reduce(true);
		builder.includeDocs(false);
		
		return builder.build();
	}
	
	private UnpaginatedRequestBuilder<?, ?> getBasicRequestBuilder(Database impl, View view, Class<?> entityType, ParametersParameterAccessor accessor) {
		final com.cloudant.client.api.views.Key.Type<?> keyType = determineKeyType(view, accessor);
		
		if (keyType == null) {
			throw new IllegalArgumentException(
				"keyType could not be inferred automatically (keys: " + Arrays.asList(view.keys()) + ", startKey: " + Arrays.asList(view.startKey()) + ", endKey: " + Arrays.asList(view.endKey()) + ")."
			);
		}
		
		final UnpaginatedRequestBuilder<?, ?> builder = createRequestBuilder(impl, view, keyType, entityType);

		builder.descending(view.descending());
		builder.group(view.group());
		
		setKeys(builder, keyType, view, accessor);

		return builder;
	}
		
	private void setKeys(UnpaginatedRequestBuilder<?, ?> builder, com.cloudant.client.api.views.Key.Type<?> keyType, View view, ParametersParameterAccessor accessor) {
		if (keyType == com.cloudant.client.api.views.Key.Type.COMPLEX) {
			final UnpaginatedRequestBuilder<ComplexKey, ?> complexKeyBuilder = (UnpaginatedRequestBuilder<ComplexKey, ?>) builder;
			
			final ComplexKey keys = parseComplexKeyArray(view.keys(), accessor);
			final ComplexKey startKey = parseComplexKeyArray(view.startKey(), accessor);
			final ComplexKey endKey = parseComplexKeyArray(view.endKey(), accessor);
			
			if (keys != null) {
				complexKeyBuilder.keys(keys);
			}
			
			if (startKey != null) {
				complexKeyBuilder.startKey(startKey);
			}
			
			if (endKey != null) {
				complexKeyBuilder.endKey(endKey);
			}
		} else {
			final Object keys = (view.keys().length > 0) ? parseKey(view.keys()[0], accessor) : null;
			final Object startKey = (view.startKey().length > 0) ? parseKey(view.startKey()[0], accessor) : null;
			final Object endKey = (view.endKey().length > 0) ? parseKey(view.endKey()[0], accessor) : null;
			
			if (keyType == com.cloudant.client.api.views.Key.Type.STRING) {
				final UnpaginatedRequestBuilder<String, ?> stringKeyBuilder = (UnpaginatedRequestBuilder<String, ?>) builder;
				
				if (keys != null) {
					stringKeyBuilder.keys((String)keys);
				}
				
				if (startKey != null) {
					stringKeyBuilder.startKey((String)startKey);
				}
				
				if (endKey != null) {
					stringKeyBuilder.endKey((String)endKey);
				}
			} else if (keyType == com.cloudant.client.api.views.Key.Type.NUMBER) {
				final UnpaginatedRequestBuilder<Number, ?> numberKeyBuilder = (UnpaginatedRequestBuilder<Number, ?>) builder;
				
				if (keys != null) {
					numberKeyBuilder.keys((Number)keys);
				}
				
				if (startKey != null) {
					numberKeyBuilder.startKey((Number)startKey);
				}
				
				if (endKey != null) {
					numberKeyBuilder.endKey((Number)endKey);
				}
				
			} else if (keyType == com.cloudant.client.api.views.Key.Type.BOOLEAN) {
				final UnpaginatedRequestBuilder<Boolean, ?> booleanKeyBuilder = (UnpaginatedRequestBuilder<Boolean, ?>) builder;
				
				if (keys != null) {
					booleanKeyBuilder.keys((Boolean)keys);
				}
				
				if (startKey != null) {
					booleanKeyBuilder.startKey((Boolean)startKey);
				}
				
				if (endKey != null) {
					booleanKeyBuilder.endKey((Boolean)endKey);
				}
			}
		}
	}
	
	private com.cloudant.client.api.views.Key.Type<?> determineKeyType(View view, ParametersParameterAccessor accessor) {
		final Key[] keys = view.keys();
		final Key[] startKey = view.startKey();
		final Key[] endKey = view.endKey();
		
		// Use complex key if there are no keys.
		if (keys.length == 0 && startKey.length == 0 && endKey.length == 0) {
			return com.cloudant.client.api.views.Key.Type.COMPLEX;
		}
		
		// Use complex key if there is at least one key with more than one keys.
		if (keys.length > 1 || startKey.length > 1 || endKey.length > 1) {
			return com.cloudant.client.api.views.Key.Type.COMPLEX;
		}
		
		final Parameters<?, ?> parameters = accessor.getParameters();
		
		// Otherwise, determine the type.
		List<Key> keyList = new ArrayList<>();
		if (view.keys().length > 0) {
			keyList.add(view.keys()[0]);
		}
		if (view.startKey().length > 0) {
			keyList.add(view.startKey()[0]);
		}
		if (view.endKey().length > 0) {
			keyList.add(view.endKey()[0]);
		}
		
		for (Key k : keyList) {
			if (StringUtils.hasLength(k.value())) {
				if (k.nullValue() || k.highSentinel()) {
					return com.cloudant.client.api.views.Key.Type.COMPLEX;
				}
				
				for (int i = 0; i < parameters.getBindableParameters().getNumberOfParameters(); i++) {
					final Parameter p = parameters.getBindableParameter(i);
				
					final String placeholder = p.getName().orElse(p.getPlaceholder());
					
					if (k.value().equals(placeholder)) {
						final Object bindable = accessor.getBindableValue(p.getIndex());
						
						if (bindable == null) {
							return null;
						}
						
						if (bindable instanceof Number) {
							return com.cloudant.client.api.views.Key.Type.NUMBER;
						}
						else if (bindable instanceof Boolean) {
							return com.cloudant.client.api.views.Key.Type.BOOLEAN;
						}
						else /*if (bindable instanceof String)*/ {
							return com.cloudant.client.api.views.Key.Type.STRING;
						}
					}
				}
			}
		}
		
		// Return complex type if no type was found
		return com.cloudant.client.api.views.Key.Type.COMPLEX;
	}
	
	private Object parseKey(Key key, ParametersParameterAccessor accessor) {
		final Parameters<?, ?> parameters = accessor.getParameters();
		
		if (StringUtils.hasLength(key.value())) {
			for (int i = 0; i < parameters.getBindableParameters().getNumberOfParameters(); i++) {
				final Parameter p = parameters.getBindableParameter(i);
			
				final String placeholder = p.getName().orElse(p.getPlaceholder());
				
				if (key.value().equals(placeholder)) {
					final Object bindable = accessor.getBindableValue(p.getIndex());
					
					if (bindable == null) {
						return null;
					}
					
					if (bindable instanceof String || bindable instanceof Number || bindable instanceof Boolean) {
						return bindable;
					} else {
						return bindable.toString();
					}
				}
			}
		}
		
		return null;
	}
	
	private ComplexKey parseComplexKeyArray(Key[] keys, ParametersParameterAccessor accessor) {
		ComplexKey key = null;
		
		if (keys.length > 0) {
			final Parameters<?, ?> parameters = accessor.getParameters();
			
			for (Key k : keys) {
				if (StringUtils.hasLength(k.value())) {
					for (int i = 0; i < parameters.getBindableParameters().getNumberOfParameters(); i++) {
						final Parameter p = parameters.getBindableParameter(i);
					
						final String placeholder = p.getName().orElse(p.getPlaceholder());
						
						if (k.value().equals(placeholder)) {
							final Object bindable = accessor.getBindableValue(p.getIndex());
							if (bindable instanceof Number) {
								if (key == null) {
									key = com.cloudant.client.api.views.Key.complex((Number) bindable);
								} else {
									key.add((Number) bindable);
								} 
							}
							else if (bindable instanceof Boolean) {
								if (key == null) {
									key = com.cloudant.client.api.views.Key.complex((Boolean) bindable);
								} else {
									key.add((Boolean) bindable);
								} 
							}
							else {
								if (key == null) {
									key = com.cloudant.client.api.views.Key.complex(bindable.toString());
								} else {
									key.add(bindable.toString());
								} 
							}
														
							break; // break inner loop
						}
					}
				}
				
				if (k.nullValue()) {
					if (key == null) {
						key = com.cloudant.client.api.views.Key.complex((String)null);
					} else {
						key.add((String) null);
					}
				}
				
				if (k.highSentinel() && key != null) {
					key.addHighSentinel();
				}
			}
		}

		return key;
	}
	
	private UnpaginatedRequestBuilder<?, ?> createRequestBuilder(Database impl, View view, com.cloudant.client.api.views.Key.Type<?> keyType, Class<?> entityType) {
		String designDocument = view.designDocument();
		if (StringUtils.isEmpty(designDocument)) {
			// try to derive design document name from entity if not set in View annotation
			designDocument = entityType.getSimpleName();
		}
		
		return impl
				.getViewRequestBuilder(designDocument, view.viewName())
				.newRequest(keyType, entityType);
	}
}
