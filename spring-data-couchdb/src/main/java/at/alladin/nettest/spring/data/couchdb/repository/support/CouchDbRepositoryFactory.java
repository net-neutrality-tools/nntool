package at.alladin.nettest.spring.data.couchdb.repository.support;

import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;

import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;
import at.alladin.nettest.spring.data.couchdb.repository.config.RepositoryOperationsMapping;
import at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation;
import at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbQueryMethod;
import at.alladin.nettest.spring.data.couchdb.repository.query.MangoBasedCouchDbQuery;
import at.alladin.nettest.spring.data.couchdb.repository.query.MapReduceBasedCouchDbQuery;
import at.alladin.nettest.spring.data.couchdb.repository.query.PartTreeMangoBasedCouchDbQuery;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbRepositoryFactory extends RepositoryFactorySupport {

	private final RepositoryOperationsMapping couchDbOperationsMapping;
	
	public CouchDbRepositoryFactory(final RepositoryOperationsMapping couchDbOperationsMapping) {
		Assert.notNull(couchDbOperationsMapping, "couchDbOperationsMapping must not be null!");
		
		this.couchDbOperationsMapping = couchDbOperationsMapping;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getEntityInformation(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T, ID> CouchDbEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		//final CouchDbPersistentEntity<?> entity = null; // TODO //mappingContext.getRequiredPersistentEntity(domainClass);
		//return new MappingCouchDbEntityInformation<>(/*(CouchDbPersistentEntity<T>) entity*/);
		return (CouchDbEntityInformation<T, ID>) new SimpleCouchDbEntityInformation<>(domainClass);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryInformation)
	 */
	@Override
	protected Object getTargetRepository(RepositoryInformation metadata) {
		final CouchDbOperations operations = couchDbOperationsMapping.resolve(metadata.getRepositoryInterface(), metadata.getDomainType());
		
		final CouchDbEntityInformation<?, String> entityInformation = getEntityInformation(metadata.getDomainType());
		
		final SimpleCouchDbRepository<?> repo = getTargetRepositoryViaReflection(metadata, entityInformation, operations);
		
		return repo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getRepositoryBaseClass(org.springframework.data.repository.core.RepositoryMetadata)
	 */
	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		if (metadata.isPagingRepository()) {
			return PagingAndSortingCouchDbRepository.class;
		} else {
			return SimpleCouchDbRepository.class;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getQueryLookupStrategy(org.springframework.data.repository.query.QueryLookupStrategy.Key, org.springframework.data.repository.query.QueryMethodEvaluationContextProvider)
	 */
	@Override
	protected Optional<QueryLookupStrategy> getQueryLookupStrategy(Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
		return Optional.of(new CouchDbQueryLookupStrategy(evaluationContextProvider));
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	private class CouchDbQueryLookupStrategy implements QueryLookupStrategy {
		
		private final QueryMethodEvaluationContextProvider evaluationContextProvider;

	    public CouchDbQueryLookupStrategy(QueryMethodEvaluationContextProvider evaluationContextProvider) {
	    	this.evaluationContextProvider = evaluationContextProvider;
	    }

		@Override
		public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
			final CouchDbOperations operations = couchDbOperationsMapping.resolve(metadata.getRepositoryInterface(), metadata.getDomainType());
			
			final CouchDbQueryMethod queryMethod = new CouchDbQueryMethod(method, metadata, factory/*, getEntityInformation(metadata.getDomainType()*/); // mappingcontext?
			//final String namedQueryName = queryMethod.getNamedQueryName(); // TODO: support named queries?
			
			if (queryMethod.hasMangoQueryAnnotation()) {
				return new MangoBasedCouchDbQuery(queryMethod, operations);
			} else if (queryMethod.hasViewAnnotation()) {
				return new MapReduceBasedCouchDbQuery(queryMethod, operations);
			}
			
			return new PartTreeMangoBasedCouchDbQuery(queryMethod, operations);
		}
	}
}
