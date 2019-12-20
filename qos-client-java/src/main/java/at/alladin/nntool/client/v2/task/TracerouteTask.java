/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
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

package at.alladin.nntool.client.v2.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import at.alladin.nntool.shared.qos.QosMeasurementType;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import at.alladin.nntool.util.tools.TracerouteService;
import at.alladin.nntool.util.tools.TracerouteService.HopDetail;

/**
 * 
 * @author lb
 *
 */
public class TracerouteTask extends AbstractQoSTask {

	public final static long DEFAULT_TIMEOUT = 10000000000L;
	
	public final static int DEFAULT_MAX_HOPS = 30;
	
	private final String host;
	
	private final long timeout;
	
	private final int maxHops;
	
	public final static String PARAM_HOST = "host";
	
	public final static String PARAM_TIMEOUT = "timeout";
	
	public final static String PARAM_MAX_HOPS = "max_hops";

	public final static String RESULT_HOST = "traceroute_objective_host";
	
	public final static String RESULT_DETAILS = "traceroute_result_details";
	
	public final static String RESULT_TIMEOUT = "traceroute_objective_timeout";
	
	public final static String RESULT_STATUS = "traceroute_result_status";
	
	public final static String RESULT_MAX_HOPS = "traceroute_objective_max_hops";
	
	public final static String RESULT_HOPS = "traceroute_result_hops";

	/**
	 * 
	 * @param taskDesc
	 */
	public TracerouteTask(QualityOfServiceTest nnTest, TaskDesc taskDesc, int threadId) {
		super(nnTest, taskDesc, threadId, threadId);
		this.host = (String)taskDesc.getParams().get(PARAM_HOST);
		
		String value = (String) taskDesc.getParams().get(PARAM_TIMEOUT);
		this.timeout = value != null ? Long.valueOf(value) : DEFAULT_TIMEOUT;
		
		value = (String) taskDesc.getParams().get(PARAM_MAX_HOPS);
		this.maxHops = value != null ? Integer.valueOf(value) : DEFAULT_MAX_HOPS;
	}

	/**
	 * 
	 */
	public QoSTestResult call() throws Exception {
  		final QoSTestResult testResult = initQoSTestResult(QosMeasurementType.TRACEROUTE);

  		testResult.getResultMap().put(RESULT_HOST, host);
  		testResult.getResultMap().put(RESULT_TIMEOUT, timeout);
  		testResult.getResultMap().put(RESULT_MAX_HOPS, maxHops);
  		
		try {
			onStart(testResult);
			final TracerouteService pingTool = getQoSTest().getTestSettings().getTracerouteServiceClazz().newInstance();
	  		pingTool.setHost(host);
	  		pingTool.setMaxHops(maxHops);

	  		final List<HopDetail> pingDetailList = new ArrayList<TracerouteService.HopDetail>();
	  		pingTool.setResultListObject(pingDetailList);

	  		final Future<List<HopDetail>> traceFuture = ClientHolder.getCommonThreadPool().submit(pingTool);
	  		
	  		try {
	  			traceFuture.get(timeout, TimeUnit.NANOSECONDS);
	  			if (!pingTool.hasMaxHopsExceeded()) {
	  				testResult.getResultMap().put(RESULT_STATUS, "OK");
		  			testResult.getResultMap().put(RESULT_HOPS, pingDetailList.size());
	  			}
	  			else {
	  				testResult.getResultMap().put(RESULT_STATUS, "MAX_HOPS_EXCEEDED");
		  			testResult.getResultMap().put(RESULT_HOPS, maxHops);
	  			}
	  		}
	  		catch (TimeoutException e) {
	  			testResult.getResultMap().put(RESULT_STATUS, "TIMEOUT");
	  		}
	  		finally {
	  			if (pingDetailList != null) {
		  			List<Map<String, Object>> resultArray = new ArrayList<>();
		  			for (final HopDetail p : pingDetailList) {
		  				resultArray.add(p.toMap());
		  			}

		  			testResult.getResultMap().put(RESULT_DETAILS, resultArray);
	  			}
	  		}
		}
		catch (Exception e) {
			e.printStackTrace();
			testResult.getResultMap().put(RESULT_STATUS, "ERROR");
		}
		finally {
			onEnd(testResult);
		}
		
        return testResult;
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.v2.task.AbstractRmbtTask#initTask()
	 */
	@Override
	public void initTask() {
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.v2.task.QoSTask#getTestType()
	 */
	public QosMeasurementType getTestType() {
		return QosMeasurementType.TRACEROUTE;
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.v2.task.QoSTask#needsQoSControlConnection()
	 */
	public boolean needsQoSControlConnection() {
		return false;
	}
}
