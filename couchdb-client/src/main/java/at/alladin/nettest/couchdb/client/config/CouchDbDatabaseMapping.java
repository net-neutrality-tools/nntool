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

package at.alladin.nettest.couchdb.client.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbDatabaseMapping {

	private final List<Database> databases = new ArrayList<>();
	
	public List<Database> getDatabases() {
		return databases;
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class Database {
		
		private String name;
		private boolean createIfNotExists;
		private final List<String> entities = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public boolean isCreateIfNotExists() {
			return createIfNotExists;
		}

		public void setCreateIfNotExists(boolean createIfNotExists) {
			this.createIfNotExists = createIfNotExists;
		}
		
		public List<String> getEntities() {
			return entities;
		}
	}
}
