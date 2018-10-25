package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This DTO class contains all QoS measurement information that is sent to the client.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO class contains all QoS measurement information that is sent to the client.")
@JsonClassDescription("This DTO class contains all QoS measurement information that is sent to the client.")
public class FullQoSMeasurement extends FullSubMeasurement {

	/**
	 * @see EvaluatedQoSResult
	 */
	@io.swagger.annotations.ApiModelProperty("This DTO class contains a single evaluated QoS objective.")
	@JsonPropertyDescription("This DTO class contains a single evaluated QoS objective.")
	@Expose
	@SerializedName("results")
	@JsonProperty("results")
	private List<EvaluatedQoSResult> results;

	public List<EvaluatedQoSResult> getResults() {
		return results;
	}

	public void setResults(List<EvaluatedQoSResult> results) {
		this.results = results;
	}
}
