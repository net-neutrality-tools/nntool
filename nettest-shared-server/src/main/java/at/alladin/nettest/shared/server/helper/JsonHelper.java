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

package at.alladin.nettest.shared.server.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

/**
 * Helper class used to generate JSON Schema from annotated classes.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface JsonHelper {

	/**
	 * This method returns a map containing JSON Schema definitions indexed by the class' simple name. 
	 * 
	 * @param classList The list of classes for which JSON Schema should be created
	 * @return A map containing the JSON Schema definitions indexed by the class' simple name.
	 */
	public static Map<String, Object> renderJsonSchemaForClasses(List<Class<?>> classList) {
		final Map<String, Object> schemaMap = new HashMap<>();
		
		final ObjectMapper objectMapper = new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				.registerModule(new JodaModule())
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		
		final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);
		
		classList.forEach(c -> {
			try {
				final JsonSchema schema = jsonSchemaGenerator.generateSchema(c);
			
				//schema.setId(c.getSimpleName());
				
				schemaMap.put(c.getSimpleName(), schema);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			}
		});
		
		return schemaMap;
	}
}
