package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb
 *
 */
@JsonClassDescription("Qos object that contains information about blocked UDP/TCP ports.")
public class QosBlockedPorts {

	public static enum QosBlockedPortType {
		UDP,
		TCP
	}
	
	/**
	 * The type of transport protocol.
	 */
	@JsonPropertyDescription("The type of transport protocol.")
	@Expose
	@SerializedName("type")
	@JsonProperty("type")
	QosBlockedPortType type;
	
	/**
	 * List of all blocked ports (incoming communication).
	 */
	@JsonPropertyDescription("List of all blocked ports (incoming communication).")
	@Expose
	@SerializedName("in_ports")
	@JsonProperty("in_ports")
	List<Integer> inPorts;

	/**
	 * List of all blocked ports (outgoing communication).
	 */
	@JsonPropertyDescription("List of all blocked ports (outgoing communication).")
	@Expose
	@SerializedName("out_ports")
	@JsonProperty("out_ports")
	List<Integer> outPorts;

	/**
	 * The amount of blocked ports (incoming communication).
	 */
	@JsonPropertyDescription("The amount of blocked ports (incoming communication).")
	@Expose
	@SerializedName("in_count")
	@JsonProperty("in_count")
	Integer inCount;
	
	/**
	 * The amount of blocked ports (outgoing communication).
	 */
	@JsonPropertyDescription("The amount of blocked ports (outgoing communication).")
	@Expose
	@SerializedName("out_count")
	@JsonProperty("out_count")
	Integer outCount;

	public QosBlockedPortType getType() {
		return type;
	}

	public void setType(QosBlockedPortType type) {
		this.type = type;
	}

	public List<Integer> getInPorts() {
		return inPorts;
	}

	public void setInPorts(List<Integer> inPorts) {
		this.inPorts = inPorts;
	}

	public List<Integer> getOutPorts() {
		return outPorts;
	}

	public void setOutPorts(List<Integer> outPorts) {
		this.outPorts = outPorts;
	}

	public Integer getInCount() {
		return inCount;
	}

	public void setInCount(Integer inCount) {
		this.inCount = inCount;
	}

	public Integer getOutCount() {
		return outCount;
	}

	public void setOutCount(Integer outCount) {
		this.outCount = outCount;
	}
}
