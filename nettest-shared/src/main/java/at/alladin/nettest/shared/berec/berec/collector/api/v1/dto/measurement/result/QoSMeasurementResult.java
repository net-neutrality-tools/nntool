package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This DTO contains the QoS measurement results from the client.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO contains the QoS measurement results from the client.")
@JsonClassDescription("This DTO contains the QoS measurement results from the client.")
public class QoSMeasurementResult extends SubMeasurementResult {

	/**
	 * QoS measurement results.
	 */
	@io.swagger.annotations.ApiModelProperty("QoS measurement results.")
	@JsonPropertyDescription("QoS measurement results.")
	@Expose
	@SerializedName("results")
	@JsonProperty("results")
	private List<Map<String, Object>> objectiveResults;

	public List<Map<String, Object>> getObjectiveResults() {
		return objectiveResults;
	}

	public void setObjectiveResults(List<Map<String, Object>> objectiveResults) {
		this.objectiveResults = objectiveResults;
	}
}
