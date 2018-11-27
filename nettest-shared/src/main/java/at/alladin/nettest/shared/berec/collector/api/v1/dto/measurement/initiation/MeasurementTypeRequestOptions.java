package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Base class for sub measurement request options sent by the measurement agent.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Base class for sub measurement request options sent by the measurement agent.")
@JsonClassDescription("Base class for sub measurement request options sent by the measurement agent.")
public abstract class MeasurementTypeRequestOptions {

	/**
	 * The test counter stored on the measurement agent (how many measurement have already been executed).
	 */
	@io.swagger.annotations.ApiModelProperty("The test counter stored on the measurement agent (how many measurement have already been executed).")
	@JsonPropertyDescription("The test counter stored on the measurement agent (how many measurement have already been executed).")
	@Expose
	@SerializedName("test_counter")
	@JsonProperty("test_counter")
	private Long testCounter;

	/**
	 * The version of the measurement library.
	 */
	@io.swagger.annotations.ApiModelProperty("The version of the measurement library.")
	@JsonPropertyDescription("The version of the measurement library.")
	@Expose
	@SerializedName("library_version")
	@JsonProperty("library_version")
	private String measurementLibraryVersion;
	
	/**
	 * The version of the measurement protocol.
	 */
	@io.swagger.annotations.ApiModelProperty("The version of the measurement protocol.")
	@JsonPropertyDescription("The version of the measurement protocol.")
	@Expose
	@SerializedName("protocol_version")
	@JsonProperty("protocol_version")
	private String measurementProtocolVersion;

	public Long getTestCounter() {
		return testCounter;
	}

	public void setTestCounter(Long testCounter) {
		this.testCounter = testCounter;
	}

	public String getMeasurementLibraryVersion() {
		return measurementLibraryVersion;
	}

	public void setMeasurementLibraryVersion(String measurementLibraryVersion) {
		this.measurementLibraryVersion = measurementLibraryVersion;
	}

	public String getMeasurementProtocolVersion() {
		return measurementProtocolVersion;
	}

	public void setMeasurementProtocolVersion(String measurementProtocolVersion) {
		this.measurementProtocolVersion = measurementProtocolVersion;
	}
}
