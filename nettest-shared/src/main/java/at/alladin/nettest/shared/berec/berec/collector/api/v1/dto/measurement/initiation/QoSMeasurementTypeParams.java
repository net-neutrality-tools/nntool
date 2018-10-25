package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;

/**
 * This DTO contains QoS measurement instructions for the client.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO contains QoS measurement instructions for the client.")
@JsonClassDescription("This DTO contains QoS measurement instructions for the client.")
public class QoSMeasurementTypeParams extends MeasurementTypeParameters {

	/**
	 * QoS objectives that should be executed by the client.
	 */
	@io.swagger.annotations.ApiModelProperty("QoS objectives that should be executed by the client.")
	@JsonPropertyDescription("QoS objectives that should be executed by the client.")
	@Expose
	@SerializedName("objectives")
	@JsonProperty("objectives")
    private Map<QoSMeasurementTypeDto, List<Map<String, Object>>> objectives = new HashMap<>();

	public Map<QoSMeasurementTypeDto, List<Map<String, Object>>> getObjectives() {
		return objectives;
	}

	public void setObjectives(Map<QoSMeasurementTypeDto, List<Map<String, Object>>> objectives) {
		this.objectives = objectives;
	}
}
