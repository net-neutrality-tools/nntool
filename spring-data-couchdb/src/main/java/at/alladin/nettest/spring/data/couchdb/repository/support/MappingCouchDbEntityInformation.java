//package at.alladin.nettest.spring.data.couchdb.repository.support;
//
//import org.springframework.data.repository.core.support.PersistentEntityInformation;
//
//import at.alladin.nettest.spring.data.couchdb.core.mapping.CouchDbPersistentEntity;
//import at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation;
//
///**
// * 
// * @author alladin-IT GmbH (bp@alladin.at)
// *
// * @param <T>
// * @param <ID>
// */
//public class MappingCouchDbEntityInformation<T, ID> extends PersistentEntityInformation<T, ID> implements CouchDbEntityInformation<T, ID> {
//
//	public MappingCouchDbEntityInformation(CouchDbPersistentEntity<T> persistentEntity) {
//		super(persistentEntity);
//	}
//
//	@Override
//	public void setId(T entity, ID id) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean hasRev(T entity) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public String getRev(T entity) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void setRev(T entity, String rev) {
//		// TODO Auto-generated method stub
//		
//	}
//}
