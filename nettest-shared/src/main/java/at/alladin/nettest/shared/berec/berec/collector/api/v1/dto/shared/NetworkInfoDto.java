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
	@io.swagger.annotations.ApiModelProperty("Contains all relevant network information of a single point in time.")
	@JsonPropertyDescription("Contains all relevant network information of a single point in time.")
	@Expose
	@SerializedName("network_point_in_time_info")
	@JsonProperty("network_point_in_time_info")
	List<NetworkPointInTimeInfoDto> networkPointInTimeInfo;
	
// _ CellLocationInfo

	/**
	 * List of captured cell information.
	 */
	@io.swagger.annotations.ApiModelProperty("List of captured cell information.")
	@JsonPropertyDescription("List of captured cell information.")
	@Expose
	@SerializedName("cell_locations")
	@JsonProperty("cell_locations")
	private List<CellLocationDto> cellLocations;
	
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

	public List<NetworkPointInTimeInfoDto> getNetworkPointInTimeInfo() {
		return networkPointInTimeInfo;
	}

	public void setNetworkPointInTimeInfo(List<NetworkPointInTimeInfoDto> networkPointInTimeInfo) {
		this.networkPointInTimeInfo = networkPointInTimeInfo;
	}

	public List<CellLocationDto> getCellLocations() {
		return cellLocations;
	}

	public void setCellLocations(List<CellLocationDto> cellLocations) {
		this.cellLocations = cellLocations;
	}

	public List<SignalDto> getSignals() {
		return signals;
	}

	public void setSignals(List<SignalDto> signals) {
		this.signals = signals;
	}
}
