package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;

/**
 * This DTO class contains a single evaluated QoS objective.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO class contains a single evaluated QoS objective.")
@JsonClassDescription("This DTO class contains a single evaluated QoS objective.")
public class EvaluatedQoSResult {

	/**
	 * @see QoSMeasurementType
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The specific QoS type (e.g. TCP, UDC)")
	@JsonPropertyDescription("The specific QoS type (e.g. TCP, UDC)")
	@Expose
	@SerializedName("type")
	@JsonProperty(required = true, value = "type")
	private QoSMeasurementTypeDto type;

	/**
	 * The already translated summary for this QoS measurement.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The already translated summary for this QoS measurement.")
	@JsonPropertyDescription("The already translated summary for this QoS measurement.")
	@Expose
	@SerializedName("summary")
	@JsonProperty(required = true, value = "summary")
	private String summary;

	/**
	 * The already translated (and evaluated) description for this QoS measurement.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The already translated (and evaluated) description for this QoS measurement.")
	@JsonPropertyDescription("The already translated (and evaluated) description for this QoS measurement.")
	@Expose
	@SerializedName("description")
	@JsonProperty(required = true, value = "description")
	private String description;

	/**
	 * The number of QoS objective evaluations that were run.
	 */
	@io.swagger.annotations.ApiModelProperty("The number of QoS objective evaluations that were run.")
	@JsonPropertyDescription("The number of QoS objective evaluations that were run.")
	@Expose
	@SerializedName("evaluation_count")
	@JsonProperty("evaluation_count")
	private Integer evaluationCount;

	/**
	 * The number of successful QoS objective evaluations.
	 */
	@io.swagger.annotations.ApiModelProperty("The number of successful QoS objective evaluations.")
	@JsonPropertyDescription("The number of successful QoS objective evaluations.")
	@Expose
	@SerializedName("success_count")
	@JsonProperty("success_count")
	private Integer successCount;

	/**
	 * The number of failed QoS objective evaluations (= evaluationCount - successCount).
	 */
	@io.swagger.annotations.ApiModelProperty("The number of failed QoS objective evaluations (= evaluationCount - successCount).")
	@JsonPropertyDescription("The number of failed QoS objective evaluations (= evaluationCount - successCount).")
	@Expose
	@SerializedName("failure_count")
	@JsonProperty("failure_count")
	private Integer failureCount;

	/**
	 * Flag to mark this QoS measurement as implausible.
	 * An implausible QoS result is one that could not have been measured that way.
	 * This flag exists for potentially tampered results, so they do not count as valid results (e.g. to prevent tampering w/statistics).
	 */
	@io.swagger.annotations.ApiModelProperty("Flag to mark this QoS measurement as implausible. An implausible QoS result is one that could not have been measured that way. This flag exists for potentially tampered results, so they do not count as valid results (e.g. to prevent tampering w/statistics).")
	@JsonPropertyDescription("Flag to mark this QoS measurement as implausible. An implausible QoS result is one that could not have been measured that way. This flag exists for potentially tampered results, so they do not count as valid results (e.g. to prevent tampering w/statistics).")
	@Expose
	@SerializedName("implausible")
	@JsonProperty("implausible")
	private boolean implausible;

	/**
	 * Contains all evaluated result keys mapped to their respective results.
	 */
	@io.swagger.annotations.ApiModelProperty("Contains all evaluated result keys mapped to their respective results.")
	@JsonPropertyDescription("Contains all evaluated result keys mapped to their respective results.")
	@Expose
	@SerializedName("evaluation_keys")
	@JsonProperty("evaluation_keys")
	private Map<String, String> evaluationKeyMap;

	public QoSMeasurementTypeDto getType() {
		return type;
	}

	public void setType(QoSMeasurementTypeDto type) {
		this.type = type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getEvaluationCount() {
		return evaluationCount;
	}

	public void setEvaluationCount(Integer evaluationCount) {
		this.evaluationCount = evaluationCount;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(Integer failureCount) {
		this.failureCount = failureCount;
	}

	public boolean isImplausible() {
		return implausible;
	}

	public void setImplausible(boolean implausible) {
		this.implausible = implausible;
	}

	public Map<String, String> getEvaluationKeyMap() {
		return evaluationKeyMap;
	}

	public void setEvaluationKeyMap(Map<String, String> evaluationKeyMap) {
		this.evaluationKeyMap = evaluationKeyMap;
	}
}
