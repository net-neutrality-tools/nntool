/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.rmbt.shared.db.QoSTestResult.TestType;
import at.alladin.rmbt.shared.hstoreparser.Hstore;
import at.alladin.rmbt.shared.qos.testscript.TestScriptInterpreter;


/**
 * holds the result string data
 * @author lb
 *
 */
public class ResultDesc implements Comparable<ResultDesc> {
	
	public final static String STATUS_CODE_FAILURE = "fail";
	
	public final static String STATUS_CODE_SUCCESS = "ok";
	
	public final static String STATUS_CODE_INFO = "info";
	
	/**
	 * 
	 */
	@SerializedName("status")
	@Expose
	private String statusCode;

	/**
	 * 
	 */
	@Expose
	private String key;
	
	/**
	 * 
	 */
	private String value;
	
	/**
	 * 
	 */
	@SerializedName("desc")
	@Expose
	private String parsedValue = null;
	
	/**
	 * 
	 */
	@SerializedName("test")
	@Expose
	private TestType testType;
	
	/**
	 * 
	 */
	private AbstractResult resultObject;
	
	/**
	 * 
	 */
	@SerializedName("uid")
	@Expose
	private List<Long> testResultUidList = new ArrayList<>();
		
	/**
	 * 
	 */
	private Hstore hstore;
	
	/**
	 * 
	 */
	private ResultOptions options;
	
	/**
	 * 
	 */
	public ResultDesc() {
		
	}
	
	/**
	 * 
	 * @param statusCode
	 * @param key
	 */
	public ResultDesc(String statusCode, String key, AbstractResult resultObject, Hstore hstore, ResultOptions options) {
		this.statusCode = statusCode;
		this.key = key;
		this.resultObject = resultObject;
		this.hstore = hstore;
		this.options = options;
	}
	
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getParsedValue() {
		return (parsedValue != null ? parsedValue : (parsedValue = String.valueOf(TestScriptInterpreter.interprete(value, hstore, resultObject, true, options))));
	}

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}
	
	public AbstractResult getResultObject() {
		return resultObject;
	}
	
	public List<Long> getTestResultUidList() {
		return testResultUidList;
	}

	public void addTestResultUid(Long testUid) {
		testResultUidList.add(testUid);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResultDesc [statusCode=" + statusCode + ", key=" + key
				+ ", value=" + value + ", testType=" + testType + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ResultDesc o) {
		//System.out.println("comparing " + this.getValue() + " : " + o.getValue());
		if (getValue() != null && o.getValue() != null && getStatusCode() != null && o.getStatusCode() != null) {
			//System.out.println("comparing with result: >>> " + this.getParsedValue() + " : " + o.getParsedValue());
			final int s = this.getStatusCode().compareTo(o.getStatusCode());
			final int i = this.getParsedValue().compareTo(o.getParsedValue());
			
			if (s==0 && i==0) {
				return 0;
			}
			
			return i==0 ? s : i;
		}
		else {
			if (this.getKey() == null || o.getKey() == null || getStatusCode() == null || o.getStatusCode() == null) {
				return 1;
			}
			else {
				final int i = this.getKey().compareTo(o.getKey());
				final int s = this.getStatusCode().compareTo(o.getStatusCode());
				if (i == 0 && s == 0) {
					return (this.getResultObject().equals(o.getResultObject()) ? 0 : 1);
				}
				return i==0 ? s : i;							
			}
		}
	}

	/**
	 * 
	 * @return
	 * @throws JSONException 
	 */
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("status", getStatusCode());
		json.put("key", getKey());
		json.put("desc", getParsedValue());
		json.put("test", getTestType());
		json.put("uid", testResultUidList);
		return json;
	}	
}
