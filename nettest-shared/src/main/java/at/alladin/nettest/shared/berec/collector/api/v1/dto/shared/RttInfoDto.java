package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains round trip time information measured during the measurement on the measurement agent.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Contains round trip time information measured during the measurement on the measurement agent.")
@JsonClassDescription("Contains round trip time information measured during the measurement on the measurement agent.")
public class RttInfoDto {

	/**
	 * List of all measured RTTs.
	 */
	@io.swagger.annotations.ApiModelProperty("List of all measured RTTs.")
	@JsonPropertyDescription("List of all measured RTTs.")
	@Expose
	@SerializedName("rtts")
	@JsonProperty("rtts")
	private List<RttDto> rtts;

	/**
	 * The number of RTT packets to send, as instructed by the server.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The number of RTT packets to send, as instructed by the server.")
	@JsonPropertyDescription("The number of RTT packets to send, as instructed by the server.")
	@Expose
	@SerializedName("requested_num_packets")
	@JsonProperty(required = true, value = "requested_num_packets")
	private Integer requestedNumPackets;
	
	/**
	 * The actual number of sent RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual number of sent RTT packets.")
	@JsonPropertyDescription("The actual number of sent RTT packets.")
	@Expose
	@SerializedName("num_sent")
	@JsonProperty(required = true, value = "num_sent")
	private Integer numSent;
	
	/**
	 * The actual number of received RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual number of received RTT packets.")
	@JsonPropertyDescription("The actual number of received RTT packets.")
	@Expose
	@SerializedName("num_received")
	@JsonProperty(required = true, value = "num_received")
	private Integer numReceived;
	
	/**
	 * The actual number of failed RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual number of failed RTT packets.")
	@JsonPropertyDescription("The actual number of failed RTT packets.")
	@Expose
	@SerializedName("num_error")
	@JsonProperty(required = true, value = "num_error")
	private Integer numError;
	
	/**
	 * The actual number of missing RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual number of missing RTT packets.")
	@JsonPropertyDescription("The actual number of missing RTT packets.")
	@Expose
	@SerializedName("num_missing")
	@JsonProperty(required = true, value = "num_missing")
	private Integer numMissing;
	
	/**
	 * The actual size of RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual size of RTT packets.")
	@JsonPropertyDescription("The actual size of RTT packets.")
	@Expose
	@SerializedName("packet_size")
	@JsonProperty(required = true, value = "packet_size")
	private Integer packetSize;

	public List<RttDto> getRtts() {
		return rtts;
	}

	public void setRtts(List<RttDto> rtts) {
		this.rtts = rtts;
	}

	public Integer getRequestedNumPackets() {
		return requestedNumPackets;
	}

	public void setRequestedNumPackets(Integer requestedNumPackets) {
		this.requestedNumPackets = requestedNumPackets;
	}

	public Integer getNumSent() {
		return numSent;
	}

	public void setNumSent(Integer numSent) {
		this.numSent = numSent;
	}

	public Integer getNumReceived() {
		return numReceived;
	}

	public void setNumReceived(Integer numReceived) {
		this.numReceived = numReceived;
	}

	public Integer getNumError() {
		return numError;
	}

	public void setNumError(Integer numError) {
		this.numError = numError;
	}

	public Integer getNumMissing() {
		return numMissing;
	}

	public void setNumMissing(Integer numMissing) {
		this.numMissing = numMissing;
	}

	public Integer getPacketSize() {
		return packetSize;
	}

	public void setPacketSize(Integer packetSize) {
		this.packetSize = packetSize;
	}
}
