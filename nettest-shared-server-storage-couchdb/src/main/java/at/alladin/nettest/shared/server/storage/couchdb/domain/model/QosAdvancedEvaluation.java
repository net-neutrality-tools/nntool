package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QosBlockedPorts.QosBlockedPortType;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
@JsonClassDescription("Model that contains advanced QoS evaluation.")
public class QosAdvancedEvaluation {

	/**
	 * see {@link QosBlockedPorts}
	 */
	@JsonPropertyDescription("Contains information about blocked ports.")
	@Expose
	@SerializedName("blocked_ports")
	@JsonProperty("blocked_ports")
	Map<QosBlockedPortType, QosBlockedPorts> blockedPorts;

	public Map<QosBlockedPortType, QosBlockedPorts> getBlockedPorts() {
		return blockedPorts;
	}

	public void setBlockedPorts(Map<QosBlockedPortType, QosBlockedPorts> blockedPorts) {
		this.blockedPorts = blockedPorts;
	}
}
