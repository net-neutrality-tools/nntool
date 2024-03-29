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

package at.alladin.nettest.spring.data.couchdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.alladin.nettest.couchdb.client.config.CouchDbDatabaseMapping;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public abstract class AbstractCouchDbDataConfiguration extends CouchDbConfigurationSupport {

	//private static final Logger logger = LoggerFactory.getLogger(AbstractCouchDbDataConfiguration.class);
	
	/**
	 * 
	 * @return
	 */
	protected abstract CouchDbConfigurer couchDbConfigurer();

	@Bean
	public CouchDbDatabaseMapping couchDbDatabaseMapping() {
		final CouchDbDatabaseMapping mapping = new CouchDbDatabaseMapping();
		
		configureCouchDbDatabaseMapping(mapping);
		
		return mapping;
	}
	
	protected void configureCouchDbDatabaseMapping(CouchDbDatabaseMapping mapping) {
		// may be overwritten by user
	}
}
