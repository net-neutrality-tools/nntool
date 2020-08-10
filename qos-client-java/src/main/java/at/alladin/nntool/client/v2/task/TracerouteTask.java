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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import at.alladin.nntool.shared.qos.QosMeasurementType;
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

	private final String authToken;

	private final long authTimestamp;

	private final long timeout;
	
	private final int maxHops;

	private final boolean isReverse;

	public final static String PARAM_HOST = "host";
	
	public final static String PARAM_TIMEOUT = "timeout";

	public final static String PARAM_MAX_HOPS = "max_hops";

	public final static String PARAM_IS_REVERSE = "is_reverse";

	public final static String PARAM_AUTH_TOKEN = "auth_token";

	public final static String PARAM_AUTH_TIMESTAMP = "auth_timestamp";

	public final static String RESULT_HOST = "traceroute_objective_host";

	public final static String RESULT_DETAILS = "traceroute_result_details";

	public final static String RESULT_TIMEOUT = "traceroute_objective_timeout";

	public final static String RESULT_IS_REVERSE = "traceroute_objective_is_reverse";

    public final static String RESULT_STATUS = "traceroute_result_status";

	public final static String RESULT_MAX_HOPS = "traceroute_objective_max_hops";

	public final static String RESULT_HOPS = "traceroute_result_hops";


    /**
     * @param taskDesc
     */
    public TracerouteTask(QualityOfServiceTest nnTest, TaskDesc taskDesc, int threadId)
    {
        super(nnTest, taskDesc, threadId, threadId);
        this.host = (String) taskDesc.getParams().get(PARAM_HOST);

        String value = (String) taskDesc.getParams().get(PARAM_TIMEOUT);
        this.timeout = value != null ? Long.parseLong(value) : DEFAULT_TIMEOUT;

        value = (String) taskDesc.getParams().get(PARAM_MAX_HOPS);
        this.maxHops = value != null ? Integer.parseInt(value) : DEFAULT_MAX_HOPS;

        this.authToken = (String) taskDesc.getParams().get(PARAM_AUTH_TOKEN);

		value = (String) taskDesc.getParams().get(PARAM_AUTH_TIMESTAMP);
		this.authTimestamp = value != null ? Long.parseLong(value) : Long.MIN_VALUE;

		Object val = taskDesc.getParams().get(PARAM_IS_REVERSE);
		this.isReverse = val != null && Boolean.parseBoolean(String.valueOf(val));

    }

	/**
	 * 
	 */
	public QoSTestResult call() throws Exception {
  		final QoSTestResult testResult = initQoSTestResult(QosMeasurementType.TRACEROUTE);

        testResult.getResultMap().put(RESULT_HOST, host);
        testResult.getResultMap().put(RESULT_TIMEOUT, timeout);
        testResult.getResultMap().put(RESULT_MAX_HOPS, maxHops);
        testResult.getResultMap().put(RESULT_IS_REVERSE, isReverse);

		try {
			onStart(testResult);

			if (isReverse) {
				doTracerouteReverse(testResult);
			}
			else {
				doTraceroute(testResult);
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

	private void doTraceroute(final QoSTestResult testResult) throws ExecutionException,
			InterruptedException, IllegalAccessException, InstantiationException {
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
			testResult.getResultMap().put(RESULT_HOPS, pingDetailList.size());
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

	private void doTracerouteReverse(final QoSTestResult testResult) {
		try {
			final Callable<String> tracerouteRunnable = new Callable() {

				@Override
				public String call() throws Exception {
					try {
						final Map<String, String> options = new HashMap<>();
						options.put("cmd", "traceroute");
						options.put("max_hops", String.valueOf(maxHops));
						options.put("tk", authToken);
						options.put("ts", String.valueOf(authTimestamp));

                        final String jsonPost = new ObjectMapper().writeValueAsString(options);

                        final URL url = new URL(TracerouteTask.this.host);
                        final HttpURLConnection httpPost;

						final long start = System.nanoTime();
						httpPost = (HttpURLConnection) url.openConnection();
						httpPost.setRequestMethod("POST");
						httpPost.setConnectTimeout((int) TimeUnit.MILLISECONDS.convert(timeout, TimeUnit.NANOSECONDS));
						httpPost.setReadTimeout((int) TimeUnit.MILLISECONDS.convert(timeout, TimeUnit.NANOSECONDS));
						httpPost.setInstanceFollowRedirects(false);
						httpPost.setRequestProperty("Content-Type", "application/json; utf-8");
						httpPost.setRequestProperty("Accept", "application/json");
						httpPost.setRequestProperty("Origin", host);
						httpPost.setDoOutput(true);

						try (final OutputStream os = httpPost.getOutputStream()) {
							final byte[] json = jsonPost.getBytes("utf-8");
							os.write(json, 0, json.length);
						}

						try (BufferedReader br = new BufferedReader(
								new InputStreamReader(httpPost.getInputStream(), "utf-8"))) {
							StringBuilder response = new StringBuilder();
							String responseLine = null;
							while ((responseLine = br.readLine()) != null) {
								response.append(responseLine.trim());
							}

							return response.toString();
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}

					return null;
				}
			};

			final Future<String> traceFuture = ClientHolder.getCommonThreadPool().submit(tracerouteRunnable);
			final String response = traceFuture.get(timeout, TimeUnit.NANOSECONDS);
			if (response != null) {
				final ReverseTracerouteResult result = new ObjectMapper().readValue(response.toString(), ReverseTracerouteResult.class);
				testResult.getResultMap().put(RESULT_STATUS, "OK");

				if (result != null) {
					List<Map<String, Object>> resultArray = new ArrayList<>();
					for (final ReverseTracerouteHop hop : result.hops) {
						resultArray.add(hop.toMap());
					}
					testResult.getResultMap().put(RESULT_DETAILS, resultArray);
				}
				testResult.getResultMap().put(RESULT_HOPS, result.hops.size());
			}
			else {
				testResult.getResultMap().put(RESULT_STATUS, "ERROR");
			}
		} catch (final TimeoutException e) {
			testResult.getResultMap().put(RESULT_HOPS, 0);
			testResult.getResultMap().put(RESULT_STATUS, "TIMEOUT");
		} catch (final Exception e) {
			e.printStackTrace();
		}
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

	private final static class ReverseTracerouteHop {
		public String id;
		public String ip;

		public Map<String, Object> toMap() {
			final Map<String, Object> res = new HashMap<>();
			res.put("host", ip);
			return res;
		}
	}

	private final static class ReverseTracerouteResult {
		public List<ReverseTracerouteHop> hops = new ArrayList<>();
	}
}
