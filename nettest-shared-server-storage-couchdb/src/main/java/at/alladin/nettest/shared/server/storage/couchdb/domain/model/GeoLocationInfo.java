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
 * Contains information about all geographic locations captured during the test.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about all geographic locations captured during the test.")
public class GeoLocationInfo {

	/**
	 * List of all captured geographic locations.
	 */
	@JsonPropertyDescription("List of all captured geographic locations.")
	@Expose
	@SerializedName("geo_locations")
	@JsonProperty("geo_locations")
	private List<GeoLocation> geoLocations;

	/**
	 * The distance moved in metres, calculated from the geoLocations.
	 */
	@JsonPropertyDescription("The distance moved in metres, calculated from the geoLocations.")
	@Expose
	@SerializedName("distance_moved_metres")
	@JsonProperty("distance_moved_metres")
	private Integer distanceMovedMetres;
	
	public List<GeoLocation> getGeoLocations() {
		return geoLocations;
	}
	
	public void setGeoLocations(List<GeoLocation> geoLocations) {
		this.geoLocations = geoLocations;
	}

	public Integer getDistanceMovedMetres() {
		return distanceMovedMetres;
	}

	public void setDistanceMovedMetres(Integer distanceMovedMetres) {
		this.distanceMovedMetres = distanceMovedMetres;
	}
}
