package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the device the measurement software is running on.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the device the measurement software is running on.")
public class DeviceInfo {

	/**
	 * @see OperatingSystemInfo
	 */
	@JsonPropertyDescription("Contains information about the client's OS.")
	@Expose
	@SerializedName("os_info")
	@JsonProperty("os_info")
	private OperatingSystemInfo osInfo;
	
	/**
	 * Device code name.
	 */
	@JsonPropertyDescription("Device code name.")
	@Expose
	@SerializedName("code_name")
	@JsonProperty("code_name")
	private String codeName;
	
    /**
     * Detailed device designation.
     */
	@JsonPropertyDescription("Detailed device designation.")
    @Expose
    @SerializedName("model")
    @JsonProperty("model")
    private String model;
    
    /**
     * The device name that is commonly known to users (e.g. Google Pixel).
     */
	@JsonPropertyDescription("The device name that is commonly known to users (e.g. Google Pixel).")
    @Expose
    @SerializedName("full_name")
    @JsonProperty("full_name")
    private String fullName;

	public OperatingSystemInfo getOsInfo() {
		return osInfo;
	}

	public void setOsInfo(OperatingSystemInfo osInfo) {
		this.osInfo = osInfo;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public String toString() {
		return "DeviceInfo [osInfo=" + osInfo + ", codeName=" + codeName + ", model=" + model + ", fullName=" + fullName
				+ "]";
	}
}