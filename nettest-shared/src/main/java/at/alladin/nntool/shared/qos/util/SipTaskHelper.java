package at.alladin.nntool.shared.qos.util;

import java.util.Map;

import com.google.common.base.Strings;

import at.alladin.nettest.shared.model.qos.QosMeasurementObjective;

public class SipTaskHelper {

	//default timeout = 5s
	public final static long DEFAULT_TIMEOUT = 5000000000L;
	
	//default call duration = 2s
	public final static long DEFAULT_CALL_DURATION = 2000000000L;
	
	public final static String PARAM_PORT = "port";
	
	public final static String PARAM_TIMEOUT = "timeout";
	
	public final static String PARAM_CALL_DURATION = "call_duration";
	
	public final static String PARAM_TO = "to";
	
	public final static String PARAM_FROM = "from";
	
	public final static String PARAM_VIA = "via";
	
	public static void preProcess(final QosMeasurementObjective objective) {
		if (objective == null || objective.getParams() == null) {
			return;
		}

		generateRandomSipAddrIfNullOrEmpty(objective.getParams(), PARAM_TO);
		generateRandomSipAddrIfNullOrEmpty(objective.getParams(), PARAM_FROM);
		generateRandomViaAddrIfNullOrEmpty(objective.getParams(), PARAM_VIA);
	}
	
	private static void generateRandomSipAddrIfNullOrEmpty(final Map<String, Object> params, final String key) {
		if (Strings.isNullOrEmpty((String)params.get(key))) {
			
		}
	}
	
	private static void generateRandomViaAddrIfNullOrEmpty(final Map<String, Object> params, final String key) {
		if (Strings.isNullOrEmpty((String)params.get(key))) {
			params.put(key, "SIP/2.0/TCP random.domain:5060");
		}
	}
}
