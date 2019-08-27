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
