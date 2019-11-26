package at.alladin.nettest.shared.server.helper;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class IpAddressHelper {

	/**
	 * HTTP header field names that can contain forwarded IP address values. 
	 * Fields are checked according to the order of this array.
	 */
	private static final String[] HTTP_HEADER_IP_FIELDS = {
	    "X-Real-IP",
	    "X-Client-IP",
		"X-FORWARDED-FOR"
	};

	private IpAddressHelper() {
		
	}
	
	/**
	 * Extract the IPv4 or IPv6 address from a HTTPServletRequest.  
	 * @param request
	 * @return
	 * @throws UnknownHostException
	 */
	public static InetAddress extractIpAddressFromHttpServletRequest(HttpServletRequest request) throws UnknownHostException {
		InetAddress ipAddressFromHttpHeader = null;
		
		for (String headerName : HTTP_HEADER_IP_FIELDS) {
			String headerValue = request.getHeader(headerName);
			if (StringUtils.hasLength(headerValue)) {
				// remove scope id if present
				if (headerValue.contains("%")) {
					headerValue = headerValue.substring(0, headerValue.indexOf('%'));
				}
				
				final InetAddress ipAddr = InetAddress.getByName(headerValue);
				
				if (ipAddr.isLoopbackAddress() || ipAddr.isLinkLocalAddress()) {
					continue;
				}
				
				ipAddressFromHttpHeader = ipAddr;
				break;
			}
		}
		
		if (ipAddressFromHttpHeader != null) {
			return ipAddressFromHttpHeader;
		}
		
		return InetAddress.getByName(request.getRemoteAddr());
	}
}
