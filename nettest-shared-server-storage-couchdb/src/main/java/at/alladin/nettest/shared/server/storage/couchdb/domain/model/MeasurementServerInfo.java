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

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains additional detail information about a measurement server.
 *  
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains additional detail information about a measurement server.")
public class MeasurementServerInfo {
	
	/**
	 * The city the measurement server is located in.
	 */
	@JsonPropertyDescription("The city the measurement server is located in.")
	@Expose
	@SerializedName("city")
	@JsonProperty("city")
	private String city;
	
	/**
	 * The country the measurement server is located in.
	 */
	@JsonPropertyDescription("The country the measurement server is located in.")
	@Expose
	@SerializedName("country")
	@JsonProperty("country")
	private String country;

	/**
	 * The geographic location of the measurement server.
	 */
	@JsonPropertyDescription("The geographic location of the measurement server.")
	@Expose
	@SerializedName("geo_location")
	@JsonProperty("geo_location")
	private GeoLocation geoLocation;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
	}
}
