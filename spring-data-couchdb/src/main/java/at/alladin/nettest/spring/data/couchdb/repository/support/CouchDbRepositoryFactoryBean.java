package at.alladin.nettest.spring.data.couchdb.repository.support;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import at.alladin.nettest.spring.data.couchdb.repository.config.RepositoryOperationsMapping;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 * @param <S>
 * @param <ID>
 */
public class CouchDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends RepositoryFactoryBeanSupport<T, S, ID> {

	private RepositoryOperationsMapping couchDbOperationsMapping;
	
	public CouchDbRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	public void setCouchDbOperationsMapping(final RepositoryOperationsMapping couchDbOperationsMapping) {
		this.couchDbOperationsMapping = couchDbOperationsMapping;
		//setMappingContext(mappingContext);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#createRepositoryFactory()
	 */
	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {
		return new CouchDbRepositoryFactory(couchDbOperationsMapping);
	}
	
	/**
	 * Make sure that the dependencies are set and not null.
	 */
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
	    Assert.notNull(couchDbOperationsMapping, "couchDbOperationsMapping must not be null!");
	}
}
