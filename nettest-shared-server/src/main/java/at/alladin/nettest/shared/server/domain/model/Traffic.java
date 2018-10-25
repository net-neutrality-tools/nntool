package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Holds information about the received and transmitted amount of data.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *	
 */
@JsonClassDescription("Holds information about the received and transmitted amount of data.")
public class Traffic {

	/**
	 * Bytes received.
	 */
	@JsonPropertyDescription("Bytes received.")
	@Expose
	@SerializedName("bytes_rx")
	@JsonProperty("bytes_rx")
	private Long bytesRx;

	/**
	 * Bytes transmitted.
	 */
	@JsonPropertyDescription("Bytes transmitted.")
	@Expose
	@SerializedName("bytes_tx")
	@JsonProperty("bytes_tx")
	private Long bytesTx;
}
