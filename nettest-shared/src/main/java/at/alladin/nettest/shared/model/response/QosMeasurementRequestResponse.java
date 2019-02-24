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

package at.alladin.nettest.shared.model.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;

/**
 * 
 * @author bp
 *
 */
@Deprecated
public class QosMeasurementRequestResponse {

	/**
	 * 
	 */
    @SerializedName("test_token")
    @Expose
    private String testToken;
    
    /**
     * 
     */
    @SerializedName("test_uuid")
    @Expose
    private String testUuid;

    /**
     * 
     */
    @Expose
    private Map<QosMeasurementType, List<Map<String, Object>>> objectives = new HashMap<>();
    
    /**
     * 
     */
    public QosMeasurementRequestResponse() {
    	
    }
    
    /**
     * 
     * @return
     *     The testToken
     */
    public String getTestToken() {
        return testToken;
    }

    /**
     * 
     * @param testToken
     *     The test_token
     */
    public void setTestToken(String testToken) {
        this.testToken = testToken;
    }

    /**
     * 
     * @return
     *     The testUuid
     */
    public String getTestUuid() {
        return testUuid;
    }

    /**
     * 
     * @param testUuid
     *     The test_uuid
     */
    public void setTestUuid(String testUuid) {
        this.testUuid = testUuid;
    }

    /**
     * 
     * @return
     */
    public Map<QosMeasurementType, List<Map<String, Object>>> getObjectives() {
		return objectives;
	}
    
    /**
     * 
     * @param objectives
     */
    public void setObjectives(Map<QosMeasurementType, List<Map<String, Object>>> objectives) {
		this.objectives = objectives;
	}

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		return "QosMeasurementRequestResponse [testToken=" + testToken + ", testUuid=" + testUuid + ", objectives="
				+ objectives + "]";
	}
}