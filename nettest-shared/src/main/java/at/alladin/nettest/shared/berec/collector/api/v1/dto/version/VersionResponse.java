package at.alladin.nettest.shared.berec.collector.api.v1.dto.version;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;

/**
 * Class for all kind of versions that the server reveals.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Class for all kind of versions that the server reveals.")
@JsonClassDescription("Class for all kind of versions that the server reveals.")
public class VersionResponse extends BasicResponse {

	/**
	 * Controller service version number.
	 */
	@io.swagger.annotations.ApiModelProperty("Controller service version number.")
	@JsonPropertyDescription("Controller service version number.")
	@Expose
	@SerializedName("controller_service_version")
	@JsonProperty("controller_service_version")
	private String controllerServiceVersion;
	
	/**
	 * Collector service version number.
	 */
	@io.swagger.annotations.ApiModelProperty("Collector service version number.")
	@JsonPropertyDescription("Collector service version number.")
	@Expose
	@SerializedName("collector_service_version")
	@JsonProperty("collector_service_version")
	private String collectorServiceVersion;
	
	/**
	 * Map service version number.
	 */
	@io.swagger.annotations.ApiModelProperty("Map service version number.")
	@JsonPropertyDescription("Map service version number.")
	@Expose
	@SerializedName("map_service_version")
	@JsonProperty("map_service_version")
	private String mapServiceVersion;
	
	/**
	 * Statistic service version number.
	 */
	@io.swagger.annotations.ApiModelProperty("Statistic service version number.")
	@JsonPropertyDescription("Statistic service version number.")
	@Expose
	@SerializedName("statistic_service_version")
	@JsonProperty("statistic_service_version")
	private String statisticServiceVersion;

	/**
	 * 
	 * @return
	 */
	public String getControllerServiceVersion() {
		return controllerServiceVersion;
	}
	
	/**
	 * 
	 * @param controllerServiceVersion
	 */
	public void setControllerServiceVersion(String controllerServiceVersion) {
		this.controllerServiceVersion = controllerServiceVersion;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCollectorServiceVersion() {
		return collectorServiceVersion;
	}

	/**
	 * 
	 * @param collectorServiceVersion
	 */
	public void setCollectorServiceVersion(String collectorServiceVersion) {
		this.collectorServiceVersion = collectorServiceVersion;
	}

	/**
	 * 
	 * @return
	 */
	public String getMapServiceVersion() {
		return mapServiceVersion;
	}

	/**
	 * 
	 * @param mapServiceVersion
	 */
	public void setMapServiceVersion(String mapServiceVersion) {
		this.mapServiceVersion = mapServiceVersion;
	}

	/**
	 * 
	 * @return
	 */
	public String getStatisticServiceVersion() {
		return statisticServiceVersion;
	}

	/**
	 * 
	 * @param statisticServiceVersion
	 */
	public void setStatisticServiceVersion(String statisticServiceVersion) {
		this.statisticServiceVersion = statisticServiceVersion;
	}
}
