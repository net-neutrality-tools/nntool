package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Holds translations for a specific language.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Holds translations for a specific language.")
public class Translation {

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
