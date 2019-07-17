package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;

/**
 * This DTO class contains all QoS measurement information that is sent to the measurement agent.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO class contains all QoS measurement information that is sent to the measurement agent.")
@JsonClassDescription("This DTO class contains all QoS measurement information that is sent to the measurement agent.")
public class FullQoSMeasurement extends FullSubMeasurement {

	/**
	 * @see EvaluatedQoSResult
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "This DTO class contains a single evaluated QoS objective.")
	@JsonPropertyDescription("This DTO class contains a single evaluated QoS objective.")
	@Expose
	@SerializedName("results")
	@JsonProperty(required = true, value = "results")
	private List<EvaluatedQoSResult> results;
	
	/**
	 * Contains the translated strings for the specific result keys.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Contains the translated strings for the specific result keys.")
	@JsonPropertyDescription("Contains the translated strings for the specific result keys.")
	@Expose
	@SerializedName("key_to_translation_map")
	@JsonProperty(required = true, value = "key_to_translation_map")
	private Map<String, String> keyToTranslationMap;

	/**
	 * Contains the translated titles and descriptions for all qos types.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Contains the translated titles and descriptions for all qos types.")
	@JsonPropertyDescription("Contains the translated titles and descriptions for all qos types.")
	@Expose
	@SerializedName("qos_type_to_description_map")
	@JsonProperty(required = true, value = "qos_type_to_description_map")
	private Map<QoSMeasurementTypeDto, QoSTypeDescription> qosTypeToDescriptionMap;

	public List<EvaluatedQoSResult> getResults() {
		return results;
	}

	public void setResults(List<EvaluatedQoSResult> results) {
		this.results = results;
	}

	public Map<String, String> getKeyToTranslationMap() {
		return keyToTranslationMap;
	}

	public void setKeyToTranslationMap(Map<String, String> keyToTranslationMap) {
		this.keyToTranslationMap = keyToTranslationMap;
	}

	public Map<QoSMeasurementTypeDto, QoSTypeDescription> getQosTypeToDescriptionMap() {
		return qosTypeToDescriptionMap;
	}

	public void setQosTypeToDescriptionMap(Map<QoSMeasurementTypeDto, QoSTypeDescription> qosTypeToDescriptionMap) {
		this.qosTypeToDescriptionMap = qosTypeToDescriptionMap;
	}
}
