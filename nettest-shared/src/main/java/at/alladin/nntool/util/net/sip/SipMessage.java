package at.alladin.nntool.util.net.sip;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author lb@alladin.at
 *
 */
public abstract class SipMessage {
		
	private static final String SIP_VERSION = "2.0";
	
	public static final String SIP_PROTOCOL_STRING = "SIP/" + SIP_VERSION;
	
	private final Map<String, String> headerMap = new HashMap<>();
	
	private byte[] body = null;
	
	/**
	 * request or status line (depends on message type: request or response)
	 * @return
	 */
	abstract String getFirstLine();
	
	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public void addHeader(final String header, final String value) {
		this.headerMap.put(header, value);
	}

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}
	
	public String getFrom() {
		return headerMap.get("FROM");
	}

	public void setFrom(String from) {
		headerMap.put("FROM", from);
	}

	public String getTo() {
		return headerMap.get("TO");
	}

	public void setTo(String to) {
		headerMap.put("TO", to);
	}

	public String getVia() {
		return headerMap.get("VIA");
	}

	public void setVia(String via) {
		headerMap.put("VIA", via);
	}
	
	public byte[] getData() {
		final String data = getDataAsString();
		if (data != null) {
			return data.getBytes();
		}

		return null;
	}

	public String getDataAsString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getFirstLine()).append("\n");

		for (final Entry<String, String> header : headerMap.entrySet()) {
			sb.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
		}

		if (body != null) {
			sb.append("Content-Length: ").append(body.length).append("\n\n");
			sb.append(body);
		}
		else {
			sb.append("Content-Length: 0").append("\n");
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return "SipMessage [headerMap=" + headerMap + ", body=" + Arrays.toString(body) + "]";
	}
}
