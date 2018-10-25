package at.alladin.nettest.service.collector.web.api.v1;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ip.IpResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;

/**
 * This controller is used to return the client's public IPv4 or IPv6 address.
 * Use an IPv6 address or DNS name with AAAA entry only to request the collector to get the public IPv6 address.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/ip")
public class IpResource {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(IpResource.class);

	/**
	 * Get client public IP address.
	 * Returns public IP address and version of requesting client.
	 *
	 * @return
	 * @throws UnknownHostException
	 */
	@io.swagger.annotations.ApiOperation(value = "Get client public IP address.", notes = "Returns public IP address and version of requesting client.")
	/*@io.swagger.annotations.ApiResponses({
		//@io.swagger.annotations.ApiResponse(code = 200, message = "OK"),
		//@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		//@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})*/
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<IpResponse>> getIp(HttpServletRequest request) throws UnknownHostException {

		final IpResponse r = new IpResponse(InetAddress.getByName(request.getRemoteAddr()));
		return ResponseHelper.ok(r);
	}
}
