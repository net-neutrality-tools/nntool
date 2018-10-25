package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Holds information about the received and transmitted amount of data on the client.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *	
 */
@io.swagger.annotations.ApiModel(description = "Holds information about the received and transmitted amount of data on the client.")
@JsonClassDescription("Holds information about the received and transmitted amount of data on the client.")
public class TrafficDto {

	/**
	 * Bytes received.
	 */
	@io.swagger.annotations.ApiModelProperty("Bytes received.")
	@JsonPropertyDescription("Bytes received.")
	@Expose
	@SerializedName("bytes_rx")
	@JsonProperty("bytes_rx")
	private Long bytesRx;

	/**
	 * Bytes transmitted.
	 */
	@io.swagger.annotations.ApiModelProperty("Bytes transmitted.")
	@JsonPropertyDescription("Bytes transmitted.")
	@Expose
	@SerializedName("bytes_tx")
	@JsonProperty("bytes_tx")
	private Long bytesTx;

	public Long getBytesRx() {
		return bytesRx;
	}

	public void setBytesRx(Long bytesRx) {
		this.bytesRx = bytesRx;
	}

	public Long getBytesTx() {
		return bytesTx;
	}

	public void setBytesTx(Long bytesTx) {
		this.bytesTx = bytesTx;
	}
}
