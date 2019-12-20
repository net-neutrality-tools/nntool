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

package at.alladin.nettest.spring.couchdb.config;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import at.alladin.nettest.couchdb.client.CouchDbClient;
import at.alladin.nettest.couchdb.client.cloudant.CloudantCouchDbClient;
import at.alladin.nettest.couchdb.client.config.CouchDbConnectionProperties;
import at.alladin.nettest.couchdb.client.config.CouchDbDatabaseMapping;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public class CouchDbConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(CouchDbConfiguration.class);

	private CouchDbProperties couchDbProperties;
	
	public CouchDbConfiguration(CouchDbProperties couchDbProperties) {
		this.couchDbProperties = couchDbProperties;
		
		logger.debug("Autoconfiguring CouchDB");
	}
	
	// We unfortunately have to support gson for cloudant client, see https://github.com/cloudant/java-cloudant/issues/352
	@Primary
	@Bean
	public GsonBuilder couchDbGsonBuilder() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		
		//gsonBuilder.setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'");
		
		// gson needs custom Jdk8 LocalDateTime converter so store it as ISO-8601 string...
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new Jdk8LocalDateTimeConverter());
		
		//Converters.registerAll(gsonBuilder);
		//Converters.registerLocalDateTime(gsonBuilder);
		//Converters.registerDateTime(gsonBuilder);
		
		return gsonBuilder;
	}
	
	@Primary
	@Bean(destroyMethod = "shutdown")
	public CouchDbClient couchDbClient() throws MalformedURLException {
		final CouchDbConnectionProperties properties = couchDbProperties.getConnection();
		return CloudantCouchDbClient.build(properties, couchDbGsonBuilder());
	}
	
	@Primary
	@Bean
	public CouchDbDatabaseMapping couchDbDatabaseMapping() {
		final CouchDbDatabaseMapping databaseMapping = new CouchDbDatabaseMapping();
		
		couchDbProperties.getDatabases().forEach(db -> {
			databaseMapping.getDatabases().add(db);
		});
		
		return databaseMapping;
	}
	
	////
	
	public static class Jdk8LocalDateTimeConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

		@Override
		public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return LocalDateTime.parse(json.getAsString());
		}

		@Override
		public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
}
