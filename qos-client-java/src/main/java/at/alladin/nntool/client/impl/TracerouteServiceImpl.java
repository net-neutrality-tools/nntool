/*******************************************************************************
 * Copyright 2016-2019 alladin-IT GmbH
 * Copyright 2020 zafaco GmbH
 * Copyright 2016 SPECURE GmbH
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

package at.alladin.nntool.client.impl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.alladin.nntool.util.tools.TracerouteService;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class TracerouteServiceImpl implements TracerouteService {
	
	public static final class PingException extends IOException {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public PingException(String msg) {
			super(msg);
		}
	}
	
	public final static class PingDetailImpl implements HopDetail {
		private final int transmitted;
		private final int received;
		private final int errors;
		private final int packetLoss;
		private String fromIp;
		private String fromIpFull;
		private List<Long> measuredTimes;
		
		private final static Pattern PATTERN_PING_PACKET = Pattern.compile("([\\d]*) packets transmitted, ([\\d]*) received, ([+-]?([\\d]*) errors, )?([\\d]*)% packet loss, time ([\\d]*)ms");
        private final static Pattern PATTERN_FROM_IP = Pattern.compile("[fF]rom ([\\.\\-_\\d\\w\\s\\(\\)]*)(:|icmp)+(.*time=([\\d\\.]*))?");
        private final static Pattern PATTERN_IP = Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
        private final static Pattern PATTERN_IPV6 = Pattern.compile(" \\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])){3}))|:)))(%.+)?\\s* ");
		
		public PingDetailImpl(String pingResult) {
			System.out.println(pingResult);

			measuredTimes = new ArrayList<>();

			final Matcher pingPacket = PATTERN_PING_PACKET.matcher(pingResult);
			
			if (pingPacket.find()) {
				transmitted = Integer.parseInt(pingPacket.group(1));
				received = Integer.parseInt(pingPacket.group(2));
				String errors = pingPacket.group(4);
				if (errors != null) {
					this.errors = Integer.parseInt(errors);
				}
				else {
					this.errors = 0;
				}
				packetLoss = Integer.parseInt(pingPacket.group(5));
			}
			else {
				transmitted = 0;
				received = 0;
				packetLoss = 0;
				errors = 0;
			}
			
			final Matcher fromIpMatcher = PATTERN_FROM_IP.matcher(pingResult);
			if (fromIpMatcher.find())
            {
                fromIpFull = fromIpMatcher.group(1);
                final Matcher ipv4Matcher = PATTERN_IP.matcher(fromIpFull);
                final Matcher ipv6Matcher = PATTERN_IPV6.matcher(fromIpFull);
                if (ipv4Matcher.find())
                {
                    fromIp = ipv4Matcher.group();
                } else if (ipv6Matcher.find())
                {
                    fromIp = ipv6Matcher.group();
                } else
                {
                    fromIp = "*";
                }
            } else
            {
                fromIpFull = "*";
                fromIp = "*";
            }	
		}

        void addTime(String ping) {
            final Matcher fromIpMatcher = PATTERN_FROM_IP.matcher(ping);
            fromIpMatcher.find();
            if (fromIpMatcher.find()) {
                do {
                    String time = fromIpMatcher.group(4);
                    if (time != null)
                    {
                        measuredTimes.add(TimeUnit.NANOSECONDS.convert((int) (Float.parseFloat(time) * 1000f), TimeUnit.MICROSECONDS));
                    }
                } while (fromIpMatcher.find());
            }
        }

		public long getTime() {
		    if(measuredTimes.isEmpty())
		        return -1;
			Long time = 0L;
			int i = 0;
			for (Long measuredTime : measuredTimes) {
				time += measuredTime;
				i++;
			}
			return time/i;
		}
		
		public List<Long> getMeasuredTimes() {
			return measuredTimes;
		}

		public int getTransmitted() {
			return transmitted;
		}

		public int getReceived() {
			return received;
		}

		public int getErrors() {
			return errors;
		}

		public int getPacketLoss() {
			return packetLoss;
		}

		public String getFromIp() {
			return fromIp;
		}

		@Override
		public String toString() {
			return "PingDetail [transmitted=" + transmitted + ", received="
					+ received + ", errors=" + errors + ", packetLoss="
					+ packetLoss + ", measuredTimes=" + measuredTimes.toString() + ", fromIp=" + fromIp
					+ "]";
		}
		
		public JSONObject toJson() {
			JSONObject json = new JSONObject();
			try {
				json.put("host", fromIp);
				json.put("time", getTime());
				return json;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

        public Map<String, Object> toMap() {
            Map<String, Object> result = new HashMap<>();
            result.put("host", fromIp);
            result.put("time", getTime());

            return result;
        }

    }
	
	private String host;
	private int maxHops;
	private int pingsPerHop;
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private boolean hasMaxHopsExceeded = true;
	private List<HopDetail> resultList;
	
	public TracerouteServiceImpl() {
		pingsPerHop = 5;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getMaxHops() {
		return maxHops;
	}

	public void setMaxHops(int maxHops) {
		this.maxHops = maxHops;
	}

	public int getPingsPerHop() {
		return pingsPerHop;
	}

	public void setPingsPerHop(int pingsPerHop) {
		this.pingsPerHop = pingsPerHop;
	}

	public List<HopDetail> call() throws Exception {
		isRunning.set(true);
		if (resultList == null) {
			resultList = new ArrayList<>();
		}

        final Runtime runtime = Runtime.getRuntime();
    	
    	for (int i = 1; i <= maxHops; i++) {
            if (Thread.interrupted() || !isRunning.get()) {
				throw new InterruptedException();
			}
			// ping  -c <count> -t <ttl>  -W <timeout> <host>
            final Process mIpAddrProcess = runtime.exec("/bin/ping -c 1 -t " + i + " -W2 " + host);
			// result: From 4.5.6.7: icmp_seq=1 Time to live exceeded
            final String proc = readFromProcess(mIpAddrProcess);
            final PingDetailImpl pingDetail = new PingDetailImpl(proc);
            resultList.add(pingDetail);
            if (pingDetail.getReceived() > 0) {
            	hasMaxHopsExceeded = false;
            	break;
            }
    	}

		// Ping each hop again and use the time measured by the ping tool instead of manual measurement
		List<TracerouteService.HopDetail> pingResultList = new ArrayList<TracerouteService.HopDetail>();
		for (HopDetail hopDetail : resultList) {
			if (!isRunning.get()) {
				throw new InterruptedException();
			}
			PingDetailImpl calculatedHopDetail = (PingDetailImpl) hopDetail;
			if (calculatedHopDetail.getFromIp().equals("*")) {
				pingResultList.add(calculatedHopDetail);
			}
			else {
				final Process mIpAddrProcess = runtime.exec("/system/bin/ping -n -i 0.2 -c " + (pingsPerHop + 1) + " -W2 " + calculatedHopDetail.getFromIp());
				final String proc = readFromProcess(mIpAddrProcess);
				calculatedHopDetail.addTime(proc);
				pingResultList.add(calculatedHopDetail);
			}
		}
    	    	
		return pingResultList;
	}
	

	/**
	 * stop the ping tool task
	 * @return true if task has been stopped or false if it was not running
	 */
	public boolean stop() {
		return isRunning.getAndSet(false);
	}
	
	public static String readFromProcess(Process proc) throws PingException {
		BufferedReader brErr = null;
		BufferedReader br = null;
		StringBuilder sbErr = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		
		try {
			brErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        	String currInputLine = null;
	        
        	while((currInputLine = brErr.readLine()) != null) {
        		sbErr.append(currInputLine);
        		sbErr.append("\n");
        	}
			
        	if (sbErr.length() > 0) {
        		throw new PingException(sbErr.toString());
        	}

			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        	currInputLine = null;
	        
        	while((currInputLine = br.readLine()) != null) {
        		sb.append(currInputLine);
        		sb.append("\n");
        	}
		}
		catch (PingException e) {
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) { }
			try {
				if (brErr != null) {
					brErr.close();
				}
			} catch (IOException e) { }
		}
		
		return sb.toString();
	}

	@Override
	public boolean hasMaxHopsExceeded() {
		return hasMaxHopsExceeded;
	}

	@Override
	public void setResultListObject(List<HopDetail> resultList) {
		this.resultList = resultList;
	}
}
