package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComputedNetworkPointInTimeInfoDto extends NetworkPointInTimeInfoDto {

	/**
	 * @see NatTypeInfo
	 */
	@JsonPropertyDescription("Contains network address translation related information.")
	@Expose
	@SerializedName("nat_type_info")
	@JsonProperty("nat_type_info")
	NatTypeInfoDto natTypeInfo;
	
	/**
	 * The computed mobile frequency band of the signal array.
	 */
	@JsonPropertyDescription("The computed mobile frequency band of the signal array.")
	@Expose
	@SerializedName("mobile_frequency")
	@JsonProperty("mobile_frequency")
	Integer mobileFrequency;
	
	public Integer getMobileFrequency() {
		return mobileFrequency;
	}

	public void setMobileFrequency(Integer mobileFrequency) {
		this.mobileFrequency = mobileFrequency;
	}

	public NatTypeInfoDto getNatTypeInfo() {
		return natTypeInfo;
	}

	public void setNatTypeInfo(NatTypeInfoDto natTypeInfo) {
		this.natTypeInfo = natTypeInfo;
	}
	
}
