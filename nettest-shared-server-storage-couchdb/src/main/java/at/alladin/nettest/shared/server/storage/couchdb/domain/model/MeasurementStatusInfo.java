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
 * Describes the status of a measurement.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Describes the status of a measurement.")
public class MeasurementStatusInfo {

	/**
	 * @see Status
	 */
	@JsonPropertyDescription("The status of a measurement.")
	@Expose
	@SerializedName("status")
	@JsonProperty("status")
	private Status status;

	/**
	 * @see Reason
	 */
	@JsonPropertyDescription("The reason why a measurement failed.")
	@Expose
	@SerializedName("reason")
	@JsonProperty("reason")
	private Reason reason;
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Reason getReason() {
		return reason;
	}
	
	public void setReason(Reason reason) {
		this.reason = reason;
	}

	/**
	 * The status of a measurement.
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static enum Status {
		STARTED,
		FINISHED,
		FAILED,
		ABORTED
	}

	/**
	 * The reason why a measurement failed.
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static enum Reason {

		/**
		 * Use this if the connection to the measurement server couldn't be established.
		 */
		UNABLE_TO_CONNECT,

		/**
		 * Use this if the connection was lost during a measurement.
		 */
		CONNECTION_LOST,

		/**
		 * Use this if the network category changed (e.g. from MOBILE to WIFI).
		 */
		NETWORK_CATEGORY_CHANGED,

		/**
		 * Use this if the App was put to background on mobile devices.
		 */
		APP_BACKGROUNDED,

		/**
		 * Use this if the user aborted the measurement.
		 */
		USER_ABORTED
	}
}
