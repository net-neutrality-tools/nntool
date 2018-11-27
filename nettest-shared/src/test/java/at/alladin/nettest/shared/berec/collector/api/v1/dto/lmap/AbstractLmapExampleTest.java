package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * TODO
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public abstract class AbstractLmapExampleTest {

	/**
	 * 
	 * @param o
	 * @return
	 */
	protected String objectToJson(Object o) {
    	ObjectMapper objectMapper = new ObjectMapper()//.registerModule(new Jdk8Module())
				//.registerModule(new JavaTimeModule())
				.registerModule(new JodaModule())
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.enable(SerializationFeature.INDENT_OUTPUT);
    	
    	try {
			return objectMapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
}
