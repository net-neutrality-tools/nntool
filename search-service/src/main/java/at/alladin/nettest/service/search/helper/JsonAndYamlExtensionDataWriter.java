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

package at.alladin.nettest.service.search.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import at.alladin.nettest.service.search.config.ExportProperties;

/**
 * Implementation of {@link ExtensionDataWriter} for JSON and YAML files.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class JsonAndYamlExtensionDataWriter implements ExtensionDataWriter {

	@Override
	public void write(List<Map<String, Object>> data, ExportProperties exportProperties, ExportExtension ext, OutputStream outputStream, boolean writeCoarseResult) throws IOException {		
		final ObjectMapper mapper = createObjectMapper(exportProperties, ext);
		
		JsonNode node;
		if (data.size() == 1) {
			node = mapper.valueToTree(data.get(0));
		} else {
			node = mapper.valueToTree(data);
		}
		
		mapper.writeValue(outputStream, node);
	}
	
	@Override
	public void write(List<Map<String, Object>> data, ExportProperties exportProperties, ExportExtension ext, OutputStream outputStream) throws IOException {
		this.write(data, exportProperties, ext, outputStream, false);
	}
	
	private ObjectMapper createObjectMapper(ExportProperties exportProperties, ExportExtension ext) {
		switch (ext) {
		case YAML: return new ObjectMapper(new YAMLFactory());
		default: return new ObjectMapper();
		}
	}
}
