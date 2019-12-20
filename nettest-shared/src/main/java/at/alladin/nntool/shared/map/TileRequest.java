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

package at.alladin.nntool.shared.map;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicRequest;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class TileRequest extends BasicRequest {

	/*@RequestParam(required=false) String highlight, 
	@RequestParam(name = "highlight_uuid", required = false) String highlightUuid, 
	@RequestParam(name = "highlight_client_uuid", required=false) String clientUuid, 
	@RequestParam(name = "statistical_method", required=false) Float quantile,
	@RequestParam(required=false) Integer period,
	@RequestParam(name = "map_options", required=false) String mapOptions, 
	@RequestParam(required=false) Integer technology, 
	@RequestParam(required=false) String provider,
	@RequestParam(required=false) String operator);*/
	
	////
	
	//private String clientUuid; // -> from ApiRequestInfo 
	
	private String highlightMeasurementUuid; // highlight 'objects' which this measurement uuid
	private String highlightClientUuid; // highlight 'objects' which this client uuid
	
	private Float statisticalMethod;
	private Integer period;
	
	private Integer networkType; // technology
	
	//private String provider;
	//private String operator;

	/**
	 * 
	 */
	public TileRequest() {
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String getHighlightMeasurementUuid() {
		return highlightMeasurementUuid;
	}
	
	/**
	 * 
	 * @param highlightMeasurementUuid
	 */
	public void setHighlightMeasurementUuid(String highlightMeasurementUuid) {
		this.highlightMeasurementUuid = highlightMeasurementUuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getHighlightClientUuid() {
		return highlightClientUuid;
	}
	
	/**
	 * 
	 * @param highlightClientUuid
	 */
	public void setHighlightClientUuid(String highlightClientUuid) {
		this.highlightClientUuid = highlightClientUuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public Float getStatisticalMethod() {
		return statisticalMethod;
	}
	
	/**
	 * 
	 * @param statisticalMethod
	 */
	public void setStatisticalMethod(Float statisticalMethod) {
		this.statisticalMethod = statisticalMethod;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getPeriod() {
		return period;
	}
	
	/**
	 * 
	 * @param period
	 */
	public void setPeriod(Integer period) {
		this.period = period;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getNetworkType() {
		return networkType;
	}
	
	/**
	 * 
	 * @param networkType
	 */
	public void setNetworkType(Integer networkType) {
		this.networkType = networkType;
	}
}
