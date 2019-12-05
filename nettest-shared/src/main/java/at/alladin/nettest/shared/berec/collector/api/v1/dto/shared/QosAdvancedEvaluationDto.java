package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QosBlockedPortsDto.QosBlockedPortTypeDto;


/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
@JsonClassDescription("Model that contains advanced QoS evaluation.")
public class QosAdvancedEvaluationDto {

	/**
	 * see {@link QosBlockedPorts}
	 */
	@JsonPropertyDescription("Contains information about blocked ports.")
	@Expose
	@SerializedName("blocked_ports")
	@JsonProperty("blocked_ports")
	Map<QosBlockedPortTypeDto, QosBlockedPortsDto> blockedPorts;

	@JsonPropertyDescription("The total number of blocked ports of all blocked port types.")
	@Expose
	@SerializedName("total_count_blocked_ports")
	@JsonProperty("total_count_blocked_ports")
	Integer totalCountBlockedPorts;

	public Map<QosBlockedPortTypeDto, QosBlockedPortsDto> getBlockedPorts() {
		return blockedPorts;
	}

	public void setBlockedPorts(Map<QosBlockedPortTypeDto, QosBlockedPortsDto> blockedPorts) {
		this.blockedPorts = blockedPorts;
	}

	public Integer getTotalCountBlockedPorts() {
		return totalCountBlockedPorts;
	}

	public void setTotalCountBlockedPorts(Integer totalCountBlockedPorts) {
		this.totalCountBlockedPorts = totalCountBlockedPorts;
	}
	
}
