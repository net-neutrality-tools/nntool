/*******************************************************************************
 * Copyright 2017-2019 alladin-IT GmbH
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

package at.alladin.nettest.shared.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb
 *
 */
@Deprecated
//@Generated("org.jsonschema2pojo")
public class Ping {

    /**
     * Client measured ping time
     * 
     * (Required)
     */
    @SerializedName("value")
    @Expose
    private Long value;
    
    /**
     * Server measured ping time
     * 
     * (Required)
     */
    @SerializedName("value_server")
    @Expose
    private Long valueServer;
    
    /**
     * TODO: Time since beginning?
     * 
     * Renamed from time_ns
     * 
     * (Required)
     */
    @SerializedName("relative_time_ns")
    @Expose
    private Long relativeTimeNs;

    /**
     * 
     * (Required)
     * 
     * @return
     *     The value
     */
    public Long getValue() {
        return value;
    }

    /**
     * 
     * (Required)
     * 
     * @param value
     *     The value
     */
    public void setValue(Long value) {
        this.value = value;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The valueServer
     */
    public Long getValueServer() {
        return valueServer;
    }

    /**
     * 
     * (Required)
     * 
     * @param valueServer
     *     The value_server
     */
    public void setValueServer(Long valueServer) {
        this.valueServer = valueServer;
    }

    /**
     * renamed from time_ns
     * (Required)
     * 
     * @return
     *     The relativeTimeNs
     */
    public Long getRelativeTimeNs() {
        return relativeTimeNs;
    }

    /**
     * renamed from time_ns
     * (Required)
     * 
     * @param relativeTimeNs
     *     The relative_time_ns
     */
    public void setRelativeTimeNs(Long relativeTimeNs) {
        this.relativeTimeNs = relativeTimeNs;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relativeTimeNs == null) ? 0 : relativeTimeNs.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((valueServer == null) ? 0 : valueServer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ping other = (Ping) obj;
		if (relativeTimeNs == null) {
			if (other.relativeTimeNs != null)
				return false;
		} else if (!relativeTimeNs.equals(other.relativeTimeNs))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (valueServer == null) {
			if (other.valueServer != null)
				return false;
		} else if (!valueServer.equals(other.valueServer))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Ping [value=" + value + ", valueServer=" + valueServer + ", relativeTimeNs=" + relativeTimeNs + "]";
	}
}