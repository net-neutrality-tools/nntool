package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains device-specific information.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains device-specific information.")
@JsonIgnoreProperties({ "_id", "_rev" })
public class Device {
	
	/**
	 * A ID which serves as primary key.
	 */
	@JsonPropertyDescription("A ID which serves as primary key.")
	@Expose
	@SerializedName("id")
	@JsonProperty("id")
	private Long id;
	
	/**
	 * Device code name.
	 */
	@JsonPropertyDescription("Device code name.")
	@Expose
	@SerializedName("code_name")
	@JsonProperty("code_name")
	private String codename;
	
	/**
     * The device name that is commonly known to users (e.g. Google Pixel).
	 */
	@JsonPropertyDescription("The device name that is commonly known to users (e.g. Google Pixel).")
	@Expose
	@SerializedName("full_name")
	@JsonProperty("full_name")
	private String fullname;
	
	public String getCodename() {
		return codename;
	}
	
	public void setCodename(String codename) {
		this.codename = codename;
	}
	
	public String getFullname() {
		return fullname;
	}
	
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@Override
	public String toString() {
		return "Device [id=" + id + ", codename=" + codename + ", fullname=" + fullname + "]";
	}
}
