package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Contains network related information gathered during the test.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Contains network related information gathered during the test.")
@JsonClassDescription("Contains network related information gathered during the test.")
public class NetworkInfoDto {
	
	/**
	 * @see NetworkPointInTimeInfoDto
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Contains all relevant network information of a single point in time.")
	@JsonPropertyDescription("Contains all relevant network information of a single point in time.")
	@Expose
	@SerializedName("network_point_in_time_info")
	@JsonProperty(required = true, value = "network_point_in_time_info")
	List<NetworkPointInTimeInfoDto> networkPointInTimeInfo;
	
// _ SignalInfo

	/**
	 * List of captured signal information.
	 */
	@io.swagger.annotations.ApiModelProperty("List of captured signal information.")
	@JsonPropertyDescription("List of captured signal information.")
	@Expose
	@SerializedName("signals")
	@JsonProperty("signals")
	private List<SignalDto> signals;
	
	/**
	 * Contains true if CGN has been detected, false otherwise.
	 * CGN detection is done by checking the traceroute test and the respective results. 
	 * According to RFC 6598 (https://tools.ietf.org/html/rfc6598'>https://tools.ietf.org/html/rfc6598)
	 * the IP address range of CGN is 100.64.0.0/10. 
	 */
	@JsonPropertyDescription("Contains true if CGN has been detected, false otherwise.")
	@Expose
	@SerializedName("is_cgn_detected")
	@JsonProperty("is_cgn_detected")
	private Boolean isCgnDetected = false;

	public List<NetworkPointInTimeInfoDto> getNetworkPointInTimeInfo() {
		return networkPointInTimeInfo;
	}

	public void setNetworkPointInTimeInfo(List<NetworkPointInTimeInfoDto> networkPointInTimeInfo) {
		this.networkPointInTimeInfo = networkPointInTimeInfo;
	}

	public List<SignalDto> getSignals() {
		return signals;
	}

	public void setSignals(List<SignalDto> signals) {
		this.signals = signals;
	}

	public Boolean getIsCgnDetected() {
		return isCgnDetected;
	}

	public void setIsCgnDetected(Boolean isCgnDetected) {
		this.isCgnDetected = isCgnDetected;
	}
	
}
