package at.alladin.nettest.service.search.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties class that contains all export related configuration options.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "search-service.export", ignoreUnknownFields = true)
public class ExportProperties {
	
	private final Prefix prefix = new Prefix();
	
	private final Zip zip = new Zip();
	
	private final Extensions extensions = new Extensions();
	
	private int maxPageSize = 100;
	
	public Zip getZip() {
		return zip;
	}
	
	public Prefix getPrefix() {
		return prefix;
	}
	
	public Extensions getExtensions() {
		return extensions;
	}
	
	public int getMaxPageSize() {
		return maxPageSize;
	}
	
	public void setMaxPageSize(int maxPageSize) {
		this.maxPageSize = maxPageSize;
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class Prefix {
		private String global;
		private String singleResult;
		private String dateRange;
		private String searchResult;
		
		public String getGlobal() {
			return global;
		}
		
		public void setGlobal(String global) {
			this.global = global;
		}
		
		public String getSingleResult() {
			return singleResult;
		}
		
		public void setSingleResult(String singleResult) {
			this.singleResult = singleResult;
		}
		
		public String getDateRange() {
			return dateRange;
		}
		
		public void setDateRange(String dateRange) {
			this.dateRange = dateRange;
		}
		
		public String getSearchResult() {
			return searchResult;
		}
		
		public void setSearchResult(String searchResult) {
			this.searchResult = searchResult;
		}
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class Zip {
		
		private List<AdditionalZipFile> additionalFiles = new ArrayList<>();
		
		public List<AdditionalZipFile> getAdditionalFiles() {
			return additionalFiles;
		}
		
		/**
		 * 
		 * @author alladin-IT GmbH (bp@alladin.at)
		 *
		 */
		public static class AdditionalZipFile {
			
			private String title;
			
			private String location;
			
			public String getTitle() {
				return title;
			}
			
			public void setTitle(String title) {
				this.title = title;
			}
			
			public String getLocation() {
				return location;
			}
			
			public void setLocation(String location) {
				this.location = location;
			}
		}
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class Extensions {
		
		private final EmptyExtension json = new EmptyExtension();
		
		private final EmptyExtension yaml = new EmptyExtension();
		
		private final CsvExtension csv = new CsvExtension();
		
		public EmptyExtension getJson() {
			return json;
		}
		
		public EmptyExtension getYaml() {
			return yaml;
		}
		
		public CsvExtension getCsv() {
			return csv;
		}
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class EmptyExtension {
		
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class CsvExtension {
		
		private final List<CsvField> fields = new ArrayList<>();
		
		public List<CsvField> getFields() {
			return fields;
		}
		
		/**
		 * 
		 * @author alladin-IT GmbH (bp@alladin.at)
		 *
		 */
		public static class CsvField {
			
			private String title;
			private String pointer;
			
			public String getTitle() {
				return title;
			}
			
			public void setTitle(String title) {
				this.title = title;
			}
			
			public String getPointer() {
				return pointer;
			}
			
			public void setPointer(String pointer) {
				this.pointer = pointer;
			}

			@Override
			public String toString() {
				return "CsvField [title=" + title + ", pointer=" + pointer + "]";
			}
		}
	}
}
