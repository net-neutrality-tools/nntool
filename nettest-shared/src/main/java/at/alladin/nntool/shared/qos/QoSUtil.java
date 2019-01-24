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

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

import at.alladin.nntool.shared.db.QoSTestResult;
import at.alladin.nntool.shared.db.QoSTestResult.TestType;
import at.alladin.nntool.shared.hstoreparser.Hstore;
import at.alladin.nntool.shared.hstoreparser.HstoreParseException;

/**
 * 
 * @author lb
 *
 */
public class QoSUtil {
    public static final Hstore HSTORE_PARSER = new Hstore(HttpProxyResult.class, NonTransparentProxyResult.class, 
    		DnsResult.class, TcpResult.class, UdpResult.class, WebsiteResult.class, VoipResult.class, TracerouteResult.class);


	/**
	 * 
	 * @author lb
	 *
	 */
	public static class TestUuid {
		public static enum UuidType {
			TEST_UUID, OPEN_TEST_UUID
		}
		
		protected UuidType type;
		protected String uuid;
		
		public TestUuid(String uuid, UuidType type) {
			this.type = type;
			this.uuid = uuid;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public UuidType getType() {
			return type;
		}

		public void setType(UuidType type) {
			this.type = type;
		}
	}
	
	/**
	 * compares test results with expected results and increases success/failure counter 
	 * @param testResult the test result
	 * @param result the parsed test result
	 * @param resultKeys result key map
	 * @param testType test type
	 * @param resultOptions result options
	 * @throws HstoreParseException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void compareTestResults(final QoSTestResult testResult, final AbstractResult result, 
			final Map<TestType,TreeSet<ResultDesc>> resultKeys, final TestType testType, final ResultOptions resultOptions) throws HstoreParseException, IllegalArgumentException, IllegalAccessException {

    	//if expected resuls not null, compare them to the test results
    	if (testResult.getExpectedResults()!=null) {    		
    		final Class<? extends AbstractResult> clazz = testType.getClazz();
    		
    		//create a parsed abstract result set sorted by priority
    		final Set<AbstractResult> expResultSet = new TreeSet<AbstractResult>(new Comparator<AbstractResult>() {
				@Override
				public int compare(final AbstractResult o1, final AbstractResult o2) {
					return o1.priority.compareTo(o2.priority);
				}
			});

    		int priority = Integer.MAX_VALUE;
    		
    		if (testResult.getExpectedResults() != null) {
	    		for (int i = 0; i < testResult.getExpectedResults().length(); i++) {
	    			final JSONObject expectedResults = testResult.getExpectedResults().optJSONObject(i);
	    			if (expectedResults != null) {
	        			//parse hstore string to object
		    			final AbstractResult expResult = QoSUtil.HSTORE_PARSER.fromJSON(expectedResults, clazz);
		    			if (expResult.getPriority() == Integer.MAX_VALUE) {
		    				expResult.setPriority(priority--);
		    			}
		    			expResultSet.add(expResult);  
	    			}
	    		}
    		}
    		
    		for (final AbstractResult expResult : expResultSet) {
    			//compare expected result to test result and save the returned id
    			ResultDesc resultDesc = ResultComparer.compare(result, expResult, QoSUtil.HSTORE_PARSER, resultOptions);
    			if (resultDesc != null) {
        			resultDesc.addTestResultUid(testResult.getUid());
        			resultDesc.setTestType(testType);
        			        			
        			final ResultHolder resultHolder = calculateResultCounter(testResult, expResult, resultDesc);

        			//check if there is a result message
        			if (resultHolder != null) {
            			TreeSet<ResultDesc> resultDescSet;
            			if (resultKeys.containsKey(testType)) {
            				resultDescSet = resultKeys.get(testType);
            			}
            			else {
            				resultDescSet = new TreeSet<>();
            				resultKeys.put(testType, resultDescSet);
            			}

        				resultDescSet.add(resultDesc);
        				
        				testResult.getResultKeyMap().put(resultDesc.getKey(), resultHolder.resultKeyType);
        				
            			if (AbstractResult.BEHAVIOUR_ABORT.equals(resultHolder.event)) {
            				break;
            			}
        			}        			
    			}
    		}
    	}
	}
	
	/**
	 * calculates and set the specific result counter
	 * @param testResult test result
	 * @param expResult expected test result
	 * @param resultDesc result description
	 * @return result type string, can be: 
	 * 		<ul>
	 * 			<li>{@link ResultDesc#STATUS_CODE_SUCCESS}</li>
	 * 			<li>{@link ResultDesc#STATUS_CODE_FAILURE}</li>
	 * 			<li>{@link ResultDesc#STATUS_CODE_INFO}</li>
	 * 		</ul>
	 */
	public static ResultHolder calculateResultCounter(final QoSTestResult testResult, final AbstractResult expResult, final ResultDesc resultDesc) {
		String resultKeyType = null;
		String event = AbstractResult.BEHAVIOUR_NOTHING;
		
		//increase the failure or success counter of this result object
		if (resultDesc.getStatusCode().equals(ResultDesc.STATUS_CODE_SUCCESS)) {
			if (expResult.getOnSuccess() != null) {
				testResult.setSuccessCounter(testResult.getSuccessCounter()+1);
				if (AbstractResult.RESULT_TYPE_DEFAULT.equals(expResult.getSuccessType())) {
					resultKeyType = ResultDesc.STATUS_CODE_SUCCESS;
				}
				else {
					resultKeyType = ResultDesc.STATUS_CODE_INFO;
				}
				
				event = expResult.getOnSuccessBehaivour();
			}
		}
		else {
			if (expResult.getOnFailure() != null) {
				testResult.setFailureCounter(testResult.getFailureCounter()+1);
				if (AbstractResult.RESULT_TYPE_DEFAULT.equals(expResult.getFailureType())) {
					resultKeyType = ResultDesc.STATUS_CODE_FAILURE;
				}
				else {
					resultKeyType = ResultDesc.STATUS_CODE_INFO;
				}
				
				event = expResult.getOnFailureBehaivour();
			}
		}
		
		return resultKeyType != null ? new ResultHolder(resultKeyType, event) : null;
	}
	
	/**
	 * 
	 * @author lb
	 *
	 */
	public static class ResultHolder {
		final String resultKeyType;
		final String event;
		
		public ResultHolder(final String resultKeyType, final String event) {
			this.resultKeyType = resultKeyType;
			this.event = event;
		}

		public String getResultKeyType() {
			return resultKeyType;
		}

		public String getEvent() {
			return event;
		}
	}
}
