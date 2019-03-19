package at.alladin.nettest.spring.data.couchdb.repository.support;

import java.util.Optional;

import org.springframework.util.Assert;

import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;
import at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public class SimpleCouchDbRepository<T> implements CouchDbRepository<T> {

	//private static final Logger logger = LoggerFactory.getLogger(SimpleCouchDbRepository.class);
	
	protected final CouchDbEntityInformation<T, String> entityInformation;
	protected final CouchDbOperations couchDbOperations;
	
	public SimpleCouchDbRepository(CouchDbEntityInformation<T, String> entityInformation, CouchDbOperations couchDbOperations) {
		this.entityInformation = entityInformation;
		this.couchDbOperations = couchDbOperations;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(S)
	 */
	@Override
	public <S extends T> S save(S entity) {
		Assert.notNull(entity, "Entity must not be null!");
		
//		return couchDbOperations.save(entity, entityInformation);
		
		if (entityInformation.isNew(entity)) {
			return couchDbOperations.save(entity, entityInformation);
		}
		
		return couchDbOperations.update(entity, entityInformation);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#saveAll(java.lang.Iterable)
	 */
	@Override
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		Assert.notNull(entities, "The given Iterable of entities must not be null!");
		
		return couchDbOperations.saveAll(entities, entityInformation);
		
		/*return Streamable.of(entities)
					.stream()
					.map(this::save)
					.collect(Collectors.toList());*/
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findById(java.lang.Object)
	 */
	@Override
	public Optional<T> findById(String id) {
		Assert.notNull(id, "The given id must not be null!");
		return Optional.ofNullable(couchDbOperations.findById(id, entityInformation.getJavaType()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#existsById(java.lang.Object)
	 */
	@Override
	public boolean existsById(String id) {
		Assert.notNull(id, "The given id must not be null!");
		return couchDbOperations.exists(id);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
	@Override
	public Iterable<T> findAll() {
		//database.query(new QueryBuilder().build(), classOfT)
		
		// TODO
		throw new UnsupportedOperationException("findAll has not yet been implemented.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAllById(java.lang.Iterable)
	 */
	@Override
	public Iterable<T> findAllById(Iterable<String> ids) {
		Assert.notNull(ids, "The given Iterable of ids must not be null!");
		
		// TODO
		throw new UnsupportedOperationException("findAllById has not yet been implemented.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#count()
	 */
	@Override
	public long count() {
		// TODO
		throw new UnsupportedOperationException("count has not yet been implemented.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#deleteById(java.lang.Object)
	 */
	@Override
	public void deleteById(String id) {
		Assert.notNull(id, "The given id must not be null!");
		
		// TODO
		throw new UnsupportedOperationException("deleteById has not yet been implemented.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
	 */
	@Override
	public void delete(T entity) {
		Assert.notNull(entity, "The given id must not be null!");
		couchDbOperations.remove(entity, entityInformation);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#deleteAll(java.lang.Iterable)
	 */
	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		Assert.notNull(entities, "The given Iterable of entities must not be null!");
		
		// TODO
		throw new UnsupportedOperationException("deleteAll has not yet been implemented.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#deleteAll()
	 */
	@Override
	public void deleteAll() {
		// TODO
		throw new UnsupportedOperationException("deleteAll has not yet been implemented.");
	}
}
