package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementType;

/**
 * This DTO is returned to the client after a successful measurement request.
 * It contains general information as well as specific information for each available measurement type.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO is returned to the client after a successful measurement request. It contains general information as well as specific information for each available measurement type.")
@JsonClassDescription("This DTO is returned to the client after a successful measurement request. It contains general information as well as specific information for each available measurement type.")
public class MeasurementInitiationResponse extends BasicResponse {

	/**
	 * The UUIDv4 identifier of the request measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("The UUIDv4 identifier of the request measurement.")
	@JsonPropertyDescription("The UUIDv4 identifier of the request measurement.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;

	/**
	 * URL of the collector which should receive the measurement result.
	 */
	@io.swagger.annotations.ApiModelProperty("URL of the collector which should receive the measurement result.")
	@JsonPropertyDescription("URL of the collector which should receive the measurement result.")
	@Expose
	@SerializedName("result_collector_base_url")
	@JsonProperty("result_collector_base_url")
	private String resultCollectorBaseUrl;

	/**
	 * Parameters for each available measurement type.
	 * These can contain special measurement instructions (e.g. stream count, duration, timeouts, ...).
	 */
	@io.swagger.annotations.ApiModelProperty("Parameters for each available measurement type. These can contain special measurement instructions (e.g. stream count, duration, timeouts, ...).")
	@JsonPropertyDescription("Parameters for each available measurement type. These can contain special measurement instructions (e.g. stream count, duration, timeouts, ...).")
	@Expose
	@SerializedName("measurement_type_params")
	@JsonProperty("measurement_type_params")
	private /*Enum*/Map<MeasurementType, MeasurementTypeParameters> mesurementTypeParameters;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getResultCollectorBaseUrl() {
		return resultCollectorBaseUrl;
	}

	public void setResultCollectorBaseUrl(String resultCollectorBaseUrl) {
		this.resultCollectorBaseUrl = resultCollectorBaseUrl;
	}

	public Map<MeasurementType, MeasurementTypeParameters> getMesurementTypeParameters() {
		return mesurementTypeParameters;
	}

	public void setMesurementTypeParameters(Map<MeasurementType, MeasurementTypeParameters> mesurementTypeParameters) {
		this.mesurementTypeParameters = mesurementTypeParameters;
	}
}
