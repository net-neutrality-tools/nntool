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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.Streamable;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 * @param <ID>
 */
public class SimpleCouchDbEntityInformation<T> implements CouchDbEntityInformation<T, String> {
	
	//private static final Logger logger = LoggerFactory.getLogger(SimpleCouchDbEntityInformation.class);
	
	private static final String[] PROPERTY_NAMES_ID = { "_id", "id", /*"uuid"*/ };
	private static final String[] PROPERTY_NAMES_REV = { "_rev", "rev", "revision" }; 
	
	protected final Class<T> javaType;
	
	protected final String docType;
	
	public SimpleCouchDbEntityInformation(Class<T> javaType) {
		this.javaType = javaType;
		
		// try to determine docType of entity
		docType = DocTypeHelper.getDocType(javaType);
		
		// TODO: set docType automatically for the entity. currently, the entity needs to map docType property...
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation#getRev(java.lang.Object)
	 */
	@Override
	public String getRev(T entity) {
		return getProperty(entity, String.class, PROPERTY_NAMES_REV);
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation#setRev(java.lang.Object, java.lang.String)
	 */
	@Override
	public void setRev(T entity, String rev) {
		setPropertyValues(entity, rev, PROPERTY_NAMES_REV);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.EntityInformation#isNew(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean isNew(T entity) {
		if (entity instanceof Persistable) {
			return ((Persistable) entity).isNew();
		}
		
		return !hasRev(entity);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.EntityInformation#getId(java.lang.Object)
	 */
	@Override
	public String getId(T entity) {
		return getProperty(entity, String.class, PROPERTY_NAMES_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation#setId(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setId(T entity, String id) {
		setPropertyValues(entity, id, PROPERTY_NAMES_ID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation#getDocType()
	 */
	@Override
	public String getDocType() {
		return docType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.EntityInformation#getIdType()
	 */
	@Override
	public Class<String> getIdType() {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.EntityMetadata#getJavaType()
	 */
	@Override
	public Class<T> getJavaType() {
		return javaType;
	}
	
	// Do we need to support Map as entity?
	private <P> P getProperty(T entity, Class<P> propertyClass, String... properties) {
		final PropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(entity);

		return Streamable.of(properties)
				.stream()
				.filter(p -> {
					if (!propertyAccessor.isReadableProperty(p)) {
						return false;
					}
					
					final Object propertyValue = propertyAccessor.getPropertyValue(p);
					return propertyValue != null && propertyClass.isInstance(propertyValue);
				})
				.findFirst()
				.map(p -> propertyClass.cast(p))
				.orElse(null);
	}
	
	// Do we need to support Map as entity?
	private <P> void setPropertyValues(T entity, P value, String... properties) {
		final PropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(entity);
		
		final Map<String, P> m = Streamable.of(properties).stream().collect(Collectors.toMap(Function.identity(), k -> value));
		
		propertyAccessor.setPropertyValues(new MutablePropertyValues(m), true, true);
	}
}
