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
