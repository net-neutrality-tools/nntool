package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;

/**
 * Holds translations for a specific language.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Holds translations for a specific language.")
public class Translation {

	@Expose
	@SerializedName("_id")
	@JsonProperty("_id")
	private String id;
	
	@Expose
	@SerializedName("_rev")
	@JsonProperty("_rev")
	private String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	private String docType;
	
	public Translation() {
		docType = DocTypeHelper.getDocType(getClass());
	}
	
	/**
	 * The language code.
	 */
	@JsonPropertyDescription("The language code.")
	@Expose
	@SerializedName("language")
	@JsonProperty("language")
    private String language;
    
    /**
     * Key/value pairs containing translations for a specific language.
     */
	@JsonPropertyDescription("Key/value pairs containing translations for a specific language.")
	@Expose
	@SerializedName("translations")
	@JsonProperty("translations")
	private Map<String, String> translations = new HashMap<>();

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRev() {
		return rev;
	}
	
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	public String getDocType() {
		return docType;
	}
	
	public void setDocType(String docType) {
		this.docType = docType;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Map<String, String> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<String, String> translations) {
		this.translations = translations;
	}
}
