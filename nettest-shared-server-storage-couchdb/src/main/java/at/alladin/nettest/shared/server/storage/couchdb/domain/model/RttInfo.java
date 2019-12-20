/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains round trip time information measured during the measurement.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Contains round trip time information measured during the measurement.")
public class RttInfo {

	/**
	 * The address of the measurement server.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The address of the rtt server.")
	@JsonPropertyDescription("The address of the rtt server.")
	@Expose
	@SerializedName("address")
	@JsonProperty(required = true, value = "address")
	private String address;
	
	/**
	 * List of all measured RTTs.
	 */
	@JsonPropertyDescription("List of all measured RTTs.")
	@Expose
	@SerializedName("rtts")
	@JsonProperty("rtts")
	private List<Rtt> rtts;

	/**
	 * The number of RTT packets to send, as instructed by the server.
	 */
	@JsonPropertyDescription("The number of RTT packets to send, as instructed by the server.")
	@Expose
	@SerializedName("requested_num_packets")
	@JsonProperty("requested_num_packets")
	private Integer requestedNumPackets;
	
	/**
	 * The actual number of sent RTT packets.
	 */
	@JsonPropertyDescription("The actual number of sent RTT packets.")
	@Expose
	@SerializedName("num_sent")
	@JsonProperty("num_sent")
	private Integer numSent;
	
	/**
	 * The actual number of received RTT packets.
	 */
	@JsonPropertyDescription("The actual number of received RTT packets.")
	@Expose
	@SerializedName("num_received")
	@JsonProperty("num_received")
	private Integer numReceived;
	
	/**
	 * The actual number of failed RTT packets.
	 */
	@JsonPropertyDescription("The actual number of failed RTT packets.")
	@Expose
	@SerializedName("num_error")
	@JsonProperty("num_error")
	private Integer numError;
	
	/**
	 * The actual number of missing RTT packets.
	 */
	@JsonPropertyDescription("The actual number of missing RTT packets.")
	@Expose
	@SerializedName("num_missing")
	@JsonProperty("num_missing")
	private Integer numMissing;
	
	/**
	 * The actual size of RTT packets.
	 */
	@JsonPropertyDescription("The actual size of RTT packets.")
	@Expose
	@SerializedName("packet_size")
	@JsonProperty("packet_size")
	private Integer packetSize;
	
	/**
	 * Minimum (best) RTT value in nanoseconds.
	 */
	@JsonPropertyDescription("Minimum (best) RTT value in nanoseconds.")
	@Expose
	@SerializedName("min_ns")
	@JsonProperty("min_ns")
	private Long minNs;
	
	/**
	 * Common logarithm of the minimum (best) RTT.
	 */
	@JsonPropertyDescription("Common logarithm of the minimum (best) RTT.")
	@Expose
	@SerializedName("min_log")
	@JsonProperty("min_log")
	private Double minLog;
	
	/**
	 * Maximum (worst) RTT value in nanoseconds.
	 */
	@JsonPropertyDescription("Maximum (worst) RTT value in nanoseconds.")
	@Expose
	@SerializedName("max_ns")
	@JsonProperty("max_ns")
	private Long maxNs;
	
	/**
	 * Common logarithm of the maximum (worst) RTT.
	 */
	@JsonPropertyDescription("Common logarithm of the maximum (worst) RTT.")
	@Expose
	@SerializedName("max_log")
	@JsonProperty("max_log")
	private Double maxLog;
	
	/**
	 * Average RTT value in nanoseconds.
	 */
	@JsonPropertyDescription("Average RTT value in nanoseconds.")
	@Expose
	@SerializedName("average_ns")
	@JsonProperty("average_ns")
	private Long averageNs;
	
	/**
	 * Common logarithm of the average RTT.
	 */
	@JsonPropertyDescription("Common logarithm of the average RTT.")
	@Expose
	@SerializedName("average_log")
	@JsonProperty("average_log")
	private Double averageLog;
	
	/**
	 * Median RTT value in nanoseconds.
	 */
	@JsonPropertyDescription("Median RTT value in nanoseconds.")
	@Expose
	@SerializedName("median_ns")
	@JsonProperty("median_ns")
	private Long medianNs;
	
	/**
	 * Common logarithm of the median RTT.
	 */
	@JsonPropertyDescription("Common logarithm of the median RTT.")
	@Expose
	@SerializedName("median_log")
	@JsonProperty("median_log")
	private Double medianLog;
	
	/**
	 * Calculated RTT variance.
	 */
	@JsonPropertyDescription("Calculated RTT variance.")
	@Expose
	@SerializedName("variance")
	@JsonProperty("variance")
	private Long variance;
	
	/**
	 * Calculated RTT standard deviation in nanoseconds.
	 */
	@JsonPropertyDescription("Calculated RTT standard deviation in nanoseconds.")
	@Expose
	@SerializedName("standard_deviation_ns")
	@JsonProperty("standard_deviation_ns")
	private Long standardDeviationNs;

	public List<Rtt> getRtts() {
		return rtts;
	}

	public void setRtts(List<Rtt> rtts) {
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

	public Long getMinNs() {
		return minNs;
	}

	public void setMinNs(Long minNs) {
		this.minNs = minNs;
	}

	public Double getMinLog() {
		return minLog;
	}

	public void setMinLog(Double minLog) {
		this.minLog = minLog;
	}

	public Long getMaxNs() {
		return maxNs;
	}

	public void setMaxNs(Long maxNs) {
		this.maxNs = maxNs;
	}

	public Double getMaxLog() {
		return maxLog;
	}

	public void setMaxLog(Double maxLog) {
		this.maxLog = maxLog;
	}

	public Long getAverageNs() {
		return averageNs;
	}

	public void setAverageNs(Long averageNs) {
		this.averageNs = averageNs;
	}

	public Double getAverageLog() {
		return averageLog;
	}

	public void setAverageLog(Double averageLog) {
		this.averageLog = averageLog;
	}

	public Long getMedianNs() {
		return medianNs;
	}

	public void setMedianNs(Long medianNs) {
		this.medianNs = medianNs;
	}

	public Double getMedianLog() {
		return medianLog;
	}

	public void setMedianLog(Double medianLog) {
		this.medianLog = medianLog;
	}

	public Long getVariance() {
		return variance;
	}

	public void setVariance(Long variance) {
		this.variance = variance;
	}

	public Long getStandardDeviationNs() {
		return standardDeviationNs;
	}

	public void setStandardDeviationNs(Long standardDeviationNs) {
		this.standardDeviationNs = standardDeviationNs;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
