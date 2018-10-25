package at.alladin.nettest.shared.server.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface JsonHelper {

	/**
	 * 
	 * @param classList
	 * @return
	 */
	public static Map<String, Object> renderJsonSchemaForClasses(List<Class<?>> classList) {
		final Map<String, Object> schemaMap = new HashMap<>();
		
		final ObjectMapper objectMapper = new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		
		final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);
		
		classList.forEach(c -> {
			try {
				JsonSchema schema = jsonSchemaGenerator.generateSchema(c);
			
				//schema.setId(c.getSimpleName());
				
				schemaMap.put(c.getSimpleName(), schema);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			}
		});
		
		return schemaMap;
	}
}
