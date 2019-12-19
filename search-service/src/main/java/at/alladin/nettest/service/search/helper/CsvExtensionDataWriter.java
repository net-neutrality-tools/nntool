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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import at.alladin.nettest.service.search.config.ExportProperties;
import at.alladin.nettest.service.search.config.ExportProperties.CsvExtension.CsvField;

/**
 * Implementation of {@link ExtensionDataWriter} for CSV files.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CsvExtensionDataWriter implements ExtensionDataWriter {

	private static final Logger logger = LoggerFactory.getLogger(CsvExtensionDataWriter.class);
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Override
	public void write(List<Map<String, Object>> data, ExportProperties exportProperties, ExportExtension ext, OutputStream outputStream) throws IOException {
		final List<CsvField> fields = exportProperties.getExtensions().getCsv().getFields();
		
		if (fields.size() == 0) {
			return;
		}
		
		final List<String> columnNames = fields.stream().map(CsvField::getTitle).collect(Collectors.toList()); // Should we translate the column names?
		
		final CsvGenerator csvGenerator = new CsvFactory().createGenerator(outputStream);
		csvGenerator.setCodec(MAPPER);
		
		final CsvSchema.Builder schemeBuilder = CsvSchema.builder().setUseHeader(true);
		
		for (String column : columnNames) {
			schemeBuilder.addColumn(column);
		}
		
		csvGenerator.setSchema(schemeBuilder.build());
		
		final ArrayNode arrayNode = MAPPER.valueToTree(data);
		
		if (!arrayNode.isArray()) {
			return;
		}
		
		csvGenerator.writeStartArray();
		
		for (final JsonNode item : arrayNode) {
			csvGenerator.writeStartObject();
			
			fields.forEach(field -> {
				try {
					final JsonNode jn = item.at(field.getPointer());
					
					csvGenerator.writeObjectField(field.getTitle(), jn);
				} catch (Exception e) {
					logger.warn("Could not set field {} (pointer: {}).", field.getTitle(), field.getPointer(), e);
				}
			});
			
			csvGenerator.writeEndObject();
		}
		
		csvGenerator.writeEndArray();
		
		csvGenerator.flush();
	}
}
