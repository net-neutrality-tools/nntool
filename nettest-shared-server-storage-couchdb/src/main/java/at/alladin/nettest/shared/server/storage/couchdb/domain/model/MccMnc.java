package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the mobile country code (MCC) and the mobile network code (MNC).
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the mobile country code (MCC) and the mobile network code (MNC).")
public class MccMnc {

	/**
	 * The mobile country code.
	 */
	@JsonPropertyDescription("The mobile country code.")
	@Expose
	@SerializedName("mcc")
	@JsonProperty("mcc")
	private Integer mcc;
	
	/**
	 * The mobile network code.
	 */
	@JsonPropertyDescription("The mobile network code.")
	@Expose
	@SerializedName("mnc")
	@JsonProperty("mnc")
	private Integer mnc;

	public Integer getMcc() {
		return mcc;
	}

	public void setMcc(Integer mcc) {
		this.mcc = mcc;
	}

	public Integer getMnc() {
		return mnc;
	}

	public void setMnc(Integer mnc) {
		this.mnc = mnc;
	}
}
