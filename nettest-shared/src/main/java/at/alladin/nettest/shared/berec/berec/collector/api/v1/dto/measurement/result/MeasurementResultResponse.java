package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;

/**
 * This DTO is returned after the client successfully submitted it's test result to the server.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO is returned after the client successfully submitted it's test result to the server.")
@JsonClassDescription("This DTO is returned after the client successfully submitted it's test result to the server.")
public class MeasurementResultResponse extends BasicResponse {

	/**
	 * The UUIDv4 identifier of the measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("The UUIDv4 identifier of the measurement.")
	@JsonPropertyDescription("The UUIDv4 identifier of the measurement.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;

	/**
	 * An UUIDv4 identifier that is used to find this measurement in an open-data context.
	 */
	@io.swagger.annotations.ApiModelProperty("An UUIDv4 identifier that is used to find this measurement in an open-data context.")
	@JsonPropertyDescription("An UUIDv4 identifier that is used to find this measurement in an open-data context.")
	@Expose
	@SerializedName("open_data_uuid")
	@JsonProperty("open_data_uuid")
	private String openDataUuid;

	/**
	 *
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 *
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 *
	 * @return
	 */
	public String getOpenDataUuid() {
		return openDataUuid;
	}

	/**
	 *
	 * @param openDataUuid
	 */
	public void setOpenDataUuid(String openDataUuid) {
		this.openDataUuid = openDataUuid;
	}
}
