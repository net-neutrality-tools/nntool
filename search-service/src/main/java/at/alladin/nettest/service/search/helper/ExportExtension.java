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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author alladin-IT GmbH (fk@alladin.at)
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public enum ExportExtension {
	CSV(new CsvExtensionDataWriter(), "csv"),
	JSON(new JsonAndYamlExtensionDataWriter(), "json"),
	YAML(new JsonAndYamlExtensionDataWriter(), "yaml", "yml");

	///
	
	private final ExtensionDataWriter writer;
	
	private final Set<String> nameSet = new HashSet<>();
	
	ExportExtension(ExtensionDataWriter writer, String ...names) {
		this.writer = writer;
		nameSet.addAll(Arrays.asList(names));
	}
	
	public ExtensionDataWriter getWriter() {
		return writer;
	}
	
	public Set<String> getNameSet() {
		return nameSet;
	}

	public String getContentType() {
		switch (this) {
		case CSV: return "text/csv";
		case JSON: return "application/json";
		case YAML: return "application/x-yaml";
		}
		
		return "text/plain";
	}
	
	/**
	 * 
	 * @return the exportExtension associated w/the given alias, or NULL if none are associated
	 */
	public static ExportExtension getByName(final String name) {
		for (ExportExtension e : ExportExtension.values()) {
			if (e.getNameSet().contains(name.toLowerCase())) {
				return e;
			}
		}
		
		return null;
	}
}
