package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

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
@JsonClassDescription("Contains network related information gathered during the test.")
public class NetworkInfo {
	
	/**
	 * @see NetworkPointInTime
	 */
	@JsonPropertyDescription("Contains all relevant network information of a single point in time.")
	@Expose
	@SerializedName("network_points_in_time")
	@JsonProperty("network_points_in_time")
	List<NetworkPointInTime> networkPointsInTime;

	/**
	 * @see ComputedNetworkPointInTime
	 */
	@JsonPropertyDescription("Computed network information.")
	@Expose
	@SerializedName("computed_network_info")
	@JsonProperty("computed_network_info")
	ComputedNetworkPointInTime computedNetworkInfo;
	
	/**
	 * @see SignalInfo
	 */
	@JsonPropertyDescription("Contains signal information captured during the test.")
	@Expose
	@SerializedName("signal_info")
	@JsonProperty("signal_info")
	private SignalInfo signalInfo;

	public List<NetworkPointInTime> getNetworkPointsInTime() {
		return networkPointsInTime;
	}

	public void setNetworkPointsInTime(List<NetworkPointInTime> networkPointsInTime) {
		this.networkPointsInTime = networkPointsInTime;
	}

	public SignalInfo getSignalInfo() {
		return signalInfo;
	}

	public void setSignalInfo(SignalInfo signalInfo) {
		this.signalInfo = signalInfo;
	}

	public ComputedNetworkPointInTime getComputedNetworkInfo() {
		return computedNetworkInfo;
	}

	public void setComputedNetworkInfo(ComputedNetworkPointInTime computedNetworkInfo) {
		this.computedNetworkInfo = computedNetworkInfo;
	}
}
