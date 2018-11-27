package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

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
@io.swagger.annotations.ApiModel(description = "Contains information about the device the measurement software is running on.")
@JsonClassDescription("Contains information about the device the measurement software is running on.")
public class DeviceInfoDto {

	/**
	 * @see OperatingSystemInfo
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Contains information about the measurement agent's OS.")
	@JsonPropertyDescription("Contains information about the measurement agent's OS.")
	@Expose
	@SerializedName("os_info")
	@JsonProperty(required = true, value = "os_info")
	private OperatingSystemInfoDto osInfo;
	
	/**
	 * Device code name.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Device code name.")
	@JsonPropertyDescription("Device code name.")
	@Expose
	@SerializedName("code_name")
	@JsonProperty(required = true, value = "code_name")
	private String codeName;
	
    /**
     * Detailed device designation.
     */
	@io.swagger.annotations.ApiModelProperty("Detailed device designation.")
	@JsonPropertyDescription("Detailed device designation.")
    @Expose
    @SerializedName("model")
    @JsonProperty("model")
    private String model;
    
    /**
     * The device name that is commonly known to users (e.g. Google Pixel).
     */
	@io.swagger.annotations.ApiModelProperty("The device name that is commonly known to users (e.g. Google Pixel).")
	@JsonPropertyDescription("The device name that is commonly known to users (e.g. Google Pixel).")
    @Expose
    @SerializedName("full_name")
    @JsonProperty("full_name")
    private String fullName;

	public OperatingSystemInfoDto getOsInfo() {
		return osInfo;
	}

	public void setOsInfo(OperatingSystemInfoDto osInfo) {
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
}
