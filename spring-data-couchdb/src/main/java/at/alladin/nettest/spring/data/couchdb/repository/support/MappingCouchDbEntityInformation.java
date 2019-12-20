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
