package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementType;

/**
 * This DTO is sent to server when requesting a measurement.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO is sent to server when requesting a measurement.")
@JsonClassDescription("This DTO is sent to server when requesting a measurement.")
public class MeasurementInitiationRequest extends BasicRequest {

	/**
	 * If the measurement is marked as anonymous it is automatically disassociated from the client.
	 */
	@io.swagger.annotations.ApiModelProperty("If the measurement is marked as anonymous it is automatically disassociated from the client.")
	@JsonPropertyDescription("If the measurement is marked as anonymous it is automatically disassociated from the client.")
	@Expose
	@SerializedName("anonymous")
	@JsonProperty("anonymous")
	private boolean anonymous;

	/**
	 * A tag provided by the client. (LMAP: capabilities.tag)
	 */
	@io.swagger.annotations.ApiModelProperty("A tag provided by the client. (LMAP: capabilities.tag)")
	@JsonPropertyDescription("A tag provided by the client. (LMAP: capabilities.tag)")
	@Expose
	@SerializedName("tag")
	@JsonProperty("tag")
	private String tag;

	/**
	 * A map filled with options for each available/requested measurement type.
	 */
	@io.swagger.annotations.ApiModelProperty("A map filled with options for each available/requested measurement type.")
	@JsonPropertyDescription("A map filled with options for each available/requested measurement type.")
	@Expose
	@SerializedName("requested_measurement_types")
	@JsonProperty(value = "requested_measurement_types", required = true)
	private /*Enum*/Map<MeasurementType, MeasurementTypeRequestOptions> requestedMeasurementTypes;

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Map<MeasurementType, MeasurementTypeRequestOptions> getRequestedMeasurementTypes() {
		return requestedMeasurementTypes;
	}

	public void setRequestedMeasurementTypes(Map<MeasurementType, MeasurementTypeRequestOptions> requestedMeasurementTypes) {
		this.requestedMeasurementTypes = requestedMeasurementTypes;
	}
}
