package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Brief/short information of a QoS measurement.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Brief/short information of a QoS measurement.")
@JsonClassDescription("Brief/short information of a QoS measurement.")
public class BriefQoSMeasurement extends BriefSubMeasurement {

	/**
	 * Indicated how many objective where run during the QoS measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("Indicated how many objective where run during the QoS measurement.")
	@JsonPropertyDescription("Indicated how many objective where run during the QoS measurement.")
	@Expose
	@SerializedName("objective_count")
	@JsonProperty("objective_count")
	private Integer objectiveCount;

}
