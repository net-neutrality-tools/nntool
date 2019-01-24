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

package at.alladin.nntool.shared.qos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nntool.shared.hstoreparser.annotation.HstoreKey;

/**
 * 
 * @author lb
 *
 */
public class HttpProxyResult extends AbstractResult {

	@HstoreKey("http_objective_url")
	@SerializedName("http_objective_url")
	@Expose
	private String target;

	@HstoreKey("http_objective_range")
	@SerializedName("http_objective_range")
	@Expose
	private String range;

	@HstoreKey("http_result_length")
	@SerializedName("http_result_length")
	@Expose
	private Long length;
	
	@HstoreKey("http_result_header")
	@SerializedName("http_result_header")
	@Expose
	private String header;
	
	@HstoreKey("http_result_status")
	@SerializedName("http_result_status")
	@Expose
	private String status;
	
	@HstoreKey("http_result_hash")
	@SerializedName("http_result_hash")
	@Expose
	private String hash;
	
	@HstoreKey("http_result_duration")
	@SerializedName("http_result_duration")
	@Expose
	private Long duration;

	public HttpProxyResult() {
		
	}
	
	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "HttpProxyResult [target=" + target + ", range=" + range
				+ ", length=" + length + ", header=" + header + ", status="
				+ status + ", hash=" + hash + ", duration=" + duration
				+ ", getOperator()=" + getOperator() + ", getOnFailure()="
				+ getOnFailure() + ", getOnSuccess()=" + getOnSuccess() + "]";
	}
}
