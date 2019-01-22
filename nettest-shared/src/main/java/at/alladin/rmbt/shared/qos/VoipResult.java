/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
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

package at.alladin.rmbt.shared.qos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.rmbt.shared.hstoreparser.annotation.HstoreKey;

/**
 * 
 * @author lb
 *
 */
public class VoipResult extends AbstractResult {
	
	@HstoreKey("voip_objective_in_port")
	@SerializedName("voip_objective_in_port")
	@Expose
	Object inPort;
	
	@HstoreKey("voip_objective_out_port")
	@SerializedName("voip_objective_out_port")
	@Expose
	Object outPort;
	
	@HstoreKey("voip_objective_call_duration")
	@SerializedName("voip_objective_call_duration")
	@Expose
	Object callDuration;
	
	@HstoreKey("voip_objective_bits_per_sample")
	@SerializedName("voip_objective_bits_per_sample")
	@Expose
	Object bitsPerSample;
	
	@HstoreKey("voip_objective_sample_rate")
	@SerializedName("voip_objective_sample_rate")
	@Expose
	Object sampleRate;
	
	@HstoreKey("voip_objective_delay")
	@SerializedName("voip_objective_delay")
	@Expose
	Object delay;
	
	@HstoreKey("voip_objective_timeout")
	@SerializedName("voip_objective_timeout")
	@Expose
	Object timeout;

	@HstoreKey("voip_objective_payload")
	@SerializedName("voip_objective_payload")
	@Expose
	Object payload;

	@HstoreKey("voip_result_in_max_jitter")
	@SerializedName("voip_result_in_max_jitter")
	@Expose
	Object maxJitterIn;

	@HstoreKey("voip_result_in_mean_jitter")
	@SerializedName("voip_result_in_mean_jitter")
	@Expose
	Object meanJitterIn;

	@HstoreKey("voip_result_in_max_delta")
	@SerializedName("voip_result_in_max_delta")
	@Expose
	Object maxDeltaIn;

	@HstoreKey("voip_result_in_num_packets")
	@SerializedName("voip_result_in_num_packets")
	@Expose
	Object numPacketsIn;

	@HstoreKey("voip_result_in_skew")
	@SerializedName("voip_result_in_skew")
	@Expose
	Object skewIn;

	@HstoreKey("voip_result_out_max_jitter")
	@SerializedName("voip_result_out_max_jitter")
	@Expose
	Object maxJitterOut;

	@HstoreKey("voip_result_out_mean_jitter")
	@SerializedName("voip_result_out_mean_jitter")
	@Expose
	Object meanJitterOut; // this variable was called "minJitter", but the json key was "..._mean_jitter", which one is correct? assuming "mean" is correct.

	@HstoreKey("voip_result_out_max_delta")
	@SerializedName("voip_result_out_max_delta")
	@Expose
	Object maxDeltaOut;

	@HstoreKey("voip_result_out_num_packets")
	@SerializedName("voip_result_out_num_packets")
	@Expose
	Object numPacketsOut;

	@HstoreKey("voip_result_out_skew")
	@SerializedName("voip_result_out_skew")
	@Expose
	Object skewOut;
	
	@HstoreKey("voip_result_in_sequence_error")
	@SerializedName("voip_result_in_sequence_error")
	@Expose
	Object seqErrorsIn;
	
	@HstoreKey("voip_result_out_sequence_error")
	@SerializedName("voip_result_out_sequence_error")
	@Expose
	Object seqErrorsOut;

	@HstoreKey("voip_result_in_short_seq")
	@SerializedName("voip_result_in_short_seq")
	@Expose
	Object shortSequenceIn;

	@HstoreKey("voip_result_out_short_seq")
	@SerializedName("voip_result_out_short_seq")
	@Expose
	Object shortSequenceOut;

	@HstoreKey("voip_result_in_long_seq")
	@SerializedName("voip_result_in_long_seq")
	@Expose
	Object longSequenceIn;

	@HstoreKey("voip_result_out_long_seq")
	@SerializedName("voip_result_out_long_seq")
	@Expose
	Object longSequenceOut;
	
	/**
	 * 
	 */
	public VoipResult() {
		
	}

	public Object getInPort() {
		return inPort;
	}

	public void setInPort(Object inPort) {
		this.inPort = inPort;
	}

	public Object getOutPort() {
		return outPort;
	}

	public void setOutPort(Object outPort) {
		this.outPort = outPort;
	}

	public Object getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(Object callDuration) {
		this.callDuration = callDuration;
	}

	public Object getBitsPerSample() {
		return bitsPerSample;
	}

	public void setBitsPerSample(Object bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}

	public Object getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Object sampleRate) {
		this.sampleRate = sampleRate;
	}

	public Object getDelay() {
		return delay;
	}

	public void setDelay(Object delay) {
		this.delay = delay;
	}

	public Object getTimeout() {
		return timeout;
	}

	public void setTimeout(Object timeout) {
		this.timeout = timeout;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public Object getMaxJitterIn() {
		return maxJitterIn;
	}

	public void setMaxJitterIn(Object maxJitterIn) {
		this.maxJitterIn = maxJitterIn;
	}

	public Object getMeanJitterIn() {
		return meanJitterIn;
	}

	public void setMeanJitterIn(Object meanJitterIn) {
		this.meanJitterIn = meanJitterIn;
	}

	public Object getMaxDeltaIn() {
		return maxDeltaIn;
	}

	public void setMaxDeltaIn(Object maxDeltaIn) {
		this.maxDeltaIn = maxDeltaIn;
	}

	public Object getNumPacketsIn() {
		return numPacketsIn;
	}

	public void setNumPacketsIn(Object numPacketsIn) {
		this.numPacketsIn = numPacketsIn;
	}

	public Object getSkewIn() {
		return skewIn;
	}

	public void setSkewIn(Object skewIn) {
		this.skewIn = skewIn;
	}

	public Object getMaxJitterOut() {
		return maxJitterOut;
	}

	public void setMaxJitterOut(Object maxJitterOut) {
		this.maxJitterOut = maxJitterOut;
	}

	public Object getMeanJitterOut() {
		return meanJitterOut;
	}

	public void setMeanJitterOut(Object meanJitterOut) {
		this.meanJitterOut = meanJitterOut;
	}

	public Object getMaxDeltaOut() {
		return maxDeltaOut;
	}

	public void setMaxDeltaOut(Object maxDeltaOut) {
		this.maxDeltaOut = maxDeltaOut;
	}

	public Object getNumPacketsOut() {
		return numPacketsOut;
	}

	public void setNumPacketsOut(Object numPacketsOut) {
		this.numPacketsOut = numPacketsOut;
	}

	public Object getSkewOut() {
		return skewOut;
	}

	public void setSkewOut(Object skewOut) {
		this.skewOut = skewOut;
	}

	public Object getSeqErrorsIn() {
		return seqErrorsIn;
	}

	public void setSeqErrorsIn(Object seqErrorsIn) {
		this.seqErrorsIn = seqErrorsIn;
	}

	public Object getSeqErrorsOut() {
		return seqErrorsOut;
	}

	public void setSeqErrorsOut(Object seqErrorsOut) {
		this.seqErrorsOut = seqErrorsOut;
	}

	public Object getShortSequenceIn() {
		return shortSequenceIn;
	}

	public void setShortSequenceIn(Object shortSequenceIn) {
		this.shortSequenceIn = shortSequenceIn;
	}

	public Object getShortSequenceOut() {
		return shortSequenceOut;
	}

	public void setShortSequenceOut(Object shortSequenceOut) {
		this.shortSequenceOut = shortSequenceOut;
	}

	public Object getLongSequenceIn() {
		return longSequenceIn;
	}

	public void setLongSequenceIn(Object longSequenceIn) {
		this.longSequenceIn = longSequenceIn;
	}

	public Object getLongSequenceOut() {
		return longSequenceOut;
	}

	public void setLongSequenceOut(Object longSequenceOut) {
		this.longSequenceOut = longSequenceOut;
	}

	@Override
	public String toString() {
		return "VoipResult [inPort=" + inPort + ", outPort=" + outPort
				+ ", callDuration=" + callDuration + ", bitsPerSample="
				+ bitsPerSample + ", sampleRate=" + sampleRate + ", delay="
				+ delay + ", timeout=" + timeout + ", payload=" + payload
				+ ", maxJitterIn=" + maxJitterIn + ", meanJitterIn="
				+ meanJitterIn + ", maxDeltaIn=" + maxDeltaIn
				+ ", numPacketsIn=" + numPacketsIn + ", skewIn=" + skewIn
				+ ", maxJitterOut=" + maxJitterOut + ", meanJitterOut="
				+ meanJitterOut + ", maxDeltaOut=" + maxDeltaOut
				+ ", numPacketsOut=" + numPacketsOut + ", skewOut=" + skewOut
				+ ", seqErrorsIn=" + seqErrorsIn + ", seqErrorsOut="
				+ seqErrorsOut + ", shortSequenceIn=" + shortSequenceIn
				+ ", shortSequenceOut=" + shortSequenceOut
				+ ", longSequenceIn=" + longSequenceIn + ", longSequenceOut="
				+ longSequenceOut + "]";
	}
}
