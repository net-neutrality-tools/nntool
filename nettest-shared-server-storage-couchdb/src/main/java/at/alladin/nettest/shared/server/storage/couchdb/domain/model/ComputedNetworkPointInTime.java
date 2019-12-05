package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains all relevant network information of a single point in time (including computed values).
 * @author lb@alladin.at
 *
 */
@JsonClassDescription("Contains all relevant network information of a single point in time (including computed values).")
public class ComputedNetworkPointInTime extends NetworkPointInTime {
	
	/**
	 * @see NatTypeInfo
	 */
	@JsonPropertyDescription("Contains network address translation related information.")
	@Expose
	@SerializedName("nat_type_info")
	@JsonProperty("nat_type_info")
	NatTypeInfo natTypeInfo;
	
	/**
	 * The computed frequency band of the signal array.
	 */
	@JsonPropertyDescription("The computed frequency band of the signal array.")
	@Expose
	@SerializedName("frequency")
	@JsonProperty("frequency")
	Integer frequency;

	public NatTypeInfo getNatTypeInfo() {
		return natTypeInfo;
	}

	public void setNatTypeInfo(NatTypeInfo natTypeInfo) {
		this.natTypeInfo = natTypeInfo;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	@Override
	public String toString() {
		return "ComputedNetworkPointInTime [natTypeInfo=" + natTypeInfo + ", frequency=" + frequency + "]";
	}

}
