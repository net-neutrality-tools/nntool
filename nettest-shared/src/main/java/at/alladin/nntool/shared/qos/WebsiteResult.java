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
 * Result example:
 * 
 * "website_result_info"=>"OK", 
 * "website_objective_url"=>"http://alladin.at", 
 * "website_result_status"=>"200", 
 * "website_result_duration"=>"2194170609", 
 * "website_result_rx_bytes"=>"18535", 
 * "website_result_tx_bytes"=>"1170", 
 * "website_objective_timeout"=>"10000"
 * 
 * 
 * @author lb
 * 
 */
public class WebsiteResult extends AbstractResult {
	
	/**
	 * 
	 */
	@HstoreKey("website_objective_url")
	@SerializedName("website_objective_url")
	@Expose
	private String url;
	
	/**
	 * 
	 */
	@HstoreKey("website_objective_clear_cache")
	@SerializedName("website_objective_clear_cache")
	@Expose
	private Boolean clearCache;
	
	/**
	 * 
	 */
	@HstoreKey("website_objective_user_agent")
	@SerializedName("website_objective_user_agent")
	@Expose
	private String userAgent;
	
	/**
	 * 
	 */
	@HstoreKey("website_objective_timeout")
	@SerializedName("website_objective_timeout")
	@Expose
	private Long timeout;
	
	////
	
	@HstoreKey("website_result_info")
	@SerializedName("website_result_info")
	@Expose
	private String info;
	
	@HstoreKey("website_result_status")
	@SerializedName("website_result_status")
	@Expose
	private String status;
	
	@HstoreKey("website_result_duration")
	@SerializedName("website_result_duration")
	@Expose
	private Long duration;
	
	@HstoreKey("website_result_rx_bytes")
	@SerializedName("website_result_rx_bytes")
	@Expose
	private Long rxBytes;
	
	@HstoreKey("website_result_tx_bytes")
	@SerializedName("website_result_tx_bytes")
	@Expose
	private Long txBytes;
	
	@HstoreKey("website_result_first_http_response_time_ns")
	@SerializedName("website_result_first_http_response_time_ns")
	@Expose
	private Long firstHttpResponseTimeNs;
	
	@HstoreKey("website_result_request_count")
	@SerializedName("website_result_request_count")
	@Expose
	private Integer requestCount;
	
	/**
	 * 
	 */
	public WebsiteResult() {
		
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Long getRxBytes() {
		return rxBytes;
	}

	public void setRxBytes(Long rxBytes) {
		this.rxBytes = rxBytes;
	}

	public Long getTxBytes() {
		return txBytes;
	}

	public void setTxBytes(Long txBytes) {
		this.txBytes = txBytes;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getClearCache() {
		return clearCache;
	}

	/**
	 * 
	 * @param clearCache
	 */
	public void setClearCache(Boolean clearCache) {
		this.clearCache = clearCache;
	}

	/**
	 * 
	 * @return
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * 
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * 
	 * @return
	 */
	public Long getFirstHttpResponseTimeNs() {
		return firstHttpResponseTimeNs;
	}

	/**
	 * 
	 * @param firstHttpResponseTimeNs
	 */
	public void setFirstHttpResponseTimeNs(Long firstHttpResponseTimeNs) {
		this.firstHttpResponseTimeNs = firstHttpResponseTimeNs;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getRequestCount() {
		return requestCount;
	}

	/**
	 * 
	 * @param requestCount
	 */
	public void setRequestCount(Integer requestCount) {
		this.requestCount = requestCount;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WebsiteResult [info=" + info + ", url=" + url + ", status="
				+ status + ", duration=" + duration + ", rxBytes=" + rxBytes
				+ ", txBytes=" + txBytes + ", timeout=" + timeout + "]";
	}
}
