package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the result of a single QoS test.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the result of a single QoS test.")
public class QoSResult {

	/**
	 * A reference to the QoS objective ID.
	 */
	@JsonPropertyDescription("A reference to the QoS objective ID.")
	@Expose
	@SerializedName("objective_id")
	@JsonProperty("objective_id")
	private Long objectiveId;

	/**
	 * The type of the given QoSResult (e.g. TCP, UDP).
	 * @see QoSMeasurementType
	 */
	@JsonPropertyDescription("The type of the given QoSResult (e.g. TCP, UDP).")
	@Expose
	@SerializedName("type")
	@JsonProperty("type")
	private QoSMeasurementType type;

	/**
	 * Stores the result key-value pairs gathered from the QoS measurement execution.
	 */
	@JsonPropertyDescription("Stores the result key-value pairs gathered from the QoS measurement execution.")
	@Expose
	@SerializedName("results")
	@JsonProperty("results")
	private Map<String, Object> results;

	/**
	 * The count of positive evaluations (successes).
	 */
	@JsonPropertyDescription("The count of positive evalutations (successes).")
	@Expose
	@SerializedName("success_count")
	@JsonProperty("success_count")
	private Integer succesCount;

	/**
	 * The count of negative evaluations (failures).
	 */
	@JsonPropertyDescription("The count of negative evalutations (failures).")
	@Expose
	@SerializedName("failure_count")
	@JsonProperty("failure_count")
	private Integer failureCount;

	/**
	 * Flag to mark this QoS measurement as implausible.
	 */
	@JsonPropertyDescription("Flag to mark this QoS measurement as implausible")
	@Expose
	@SerializedName("implausible")
	@JsonProperty("implausible")
	private boolean implausible;

	public Long getObjectiveId() {
		return objectiveId;
	}

	public void setObjectiveId(Long objectiveId) {
		this.objectiveId = objectiveId;
	}

	public QoSMeasurementType getType() {
		return type;
	}

	public void setType(QoSMeasurementType type) {
		this.type = type;
	}

	public Map<String, Object> getResults() {
		return results;
	}

	public void setResults(Map<String, Object> results) {
		this.results = results;
	}

	public Integer getSuccesCount() {
		return succesCount;
	}

	public void setSuccesCount(Integer succesCount) {
		this.succesCount = succesCount;
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
}
