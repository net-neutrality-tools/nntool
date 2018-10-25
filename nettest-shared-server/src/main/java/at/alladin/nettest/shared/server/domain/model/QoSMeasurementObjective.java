package at.alladin.nettest.shared.server.domain.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * A QoS objective defines one QoS measurement with type, id, concurrency group, parameters, etc.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("A QoS objective defines one QoS measurement with type, id, concurrency group, parameters, etc.")
public class QoSMeasurementObjective {

	/**
	 * An ID which serves as primary key.
	 */
	@JsonPropertyDescription("An ID which serves as primary key.")
	@Expose
	@SerializedName("id")
	@JsonProperty("id")
	private String id;
	
	/**
	 * Flag if this QoS measurement objective is enabled.
	 */
	@JsonPropertyDescription("Flag if this QoS measurement objective is enabled.")
	@Expose
	@SerializedName("enabled")
	@JsonProperty("enabled")
	private boolean enabled;

	/**
	 * @see QoSMeasurementType
	 */
	@JsonPropertyDescription("Enum that holds the names of the available QoS measurement objectives.")
	@Expose
	@SerializedName("type")
	@JsonProperty("type")
	private QoSMeasurementType type;
	
	/**
	 * Objectives are ordered based on the concurrency group.
	 * Objectives that have the same concurrency group will be executed in parallel.
	 * Concurrency groups will be executed after each other in sorted order.
	 */
	@JsonPropertyDescription("Objectives are ordered based on the concurrency group. Objectives that have the same concurrency group will be executed in parallel. Concurrency groups will be executed after each other in sorted order.")
	@Expose
	@SerializedName("concurrency_group")
	@JsonProperty("concurrency_group")
	private Long concurrencyGroup;
	
	/**
	 * UUID of the measurement server that is used in the execution of this objective or null if the
	 * objective doesn't require a server.
	 */
	@JsonPropertyDescription("UUID of the measurement server that is used in the execution of this objective or null if the objective doesn't require a server.")
	@Expose
	@SerializedName("measurement_server_uuid")
	@JsonProperty("measurement_server_uuid")
	private String measurementServerUuid;
	
	/**
	 * @see QosTranslationKeys
	 */
	@JsonPropertyDescription("Stores the translation keys for QoS objectives.")
	@Expose
	@SerializedName("translation_keys")
	@JsonProperty("translation_keys")
	private QoSTranslationKeys translationKeys;
	
	/**
	 * A map of QoS objective parameters (e.g. timeout, TCP port, web page URL) 
	 */
	@JsonPropertyDescription("A map of QoS objective parameters (e.g. timeout, TCP port, web page URL) ")
	@Expose
	@SerializedName("parameters")
	@JsonProperty("parameters")
	private Map<String, Object> parameters;
	
	/**
	 * A list of custom evaluations for this QoS objective.
	 * Evaluations are used to present human readable results to the end user.
	 * Example:
	 *  [
	 *   {
	 *     "operator": "eq",
	 *     "on_failure": "ntp.failure",
	 *     "nontransproxy_result_response": "%PARAM nontransproxy_objective_request%",
	 *     "on_success": "ntp.success"
	 *   }
	 * ]
	 */
	@JsonPropertyDescription("A list of custom evaluations for this QoS objective. Evaluations are used to present human readable results to the end user.")
	@Expose
	@SerializedName("evaluations")
	@JsonProperty("evaluations")
	private List<Map<String, String>> evaluations;
	
	/**
	 * Stores the translation keys for QoS objectives.
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@JsonClassDescription("Stores the translation keys for QoS objectives.")
	public static class QoSTranslationKeys {
		
		/**
		 * Translation key name for the summary of this QoS objective.
		 */
		@JsonPropertyDescription("Translation key name for the summary of this QoS objective.")
		@Expose
		@SerializedName("summary")
		@JsonProperty("summary")
		private String summary;
		
		/**
		 * Translation key name for the description of this QoS objective.
		 */
		@JsonPropertyDescription("Translation key name for the description of this QoS objective.")
		@Expose
		@SerializedName("description")
		@JsonProperty("description")
		private String description;
	}
}
