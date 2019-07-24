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

	public NatTypeInfo getNatTypeInfo() {
		return natTypeInfo;
	}

	public void setNatTypeInfo(NatTypeInfo natTypeInfo) {
		this.natTypeInfo = natTypeInfo;
	}

	@Override
	public String toString() {
		return "ComputedNetworkPointInTime [natTypeInfo=" + natTypeInfo + ", toString()=" + super.toString() + "]";
	}
}
