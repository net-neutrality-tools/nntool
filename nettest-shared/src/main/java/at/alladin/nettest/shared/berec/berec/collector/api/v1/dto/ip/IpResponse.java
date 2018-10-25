package at.alladin.nettest.shared.berec.collector.api.v1.dto.ip;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Response object sent to the client after a successful IP request.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Response object sent to the client after a successful IP request.")
@JsonClassDescription("Response object sent to the client after a successful IP request.")
public class IpResponse {

	/**
	 * The client's public IP address.
	 */
	@io.swagger.annotations.ApiModelProperty("The client's public IP address.")
	@JsonPropertyDescription("The client's public IP address.")
	@Expose
	@SerializedName("ip_address")
	@JsonProperty("ip_address")
	private String ipAddress;
	
	/**
	 * The client's public IP version (IPv4 or IPv6).
	 */
	@io.swagger.annotations.ApiModelProperty("The client's public IP version (IPv4 or IPv6).")
	@JsonPropertyDescription("The client's public IP version (IPv4 or IPv6).")
	@Expose
	@SerializedName("ip_version")
	@JsonProperty("ip_version")
	private IpVersion version;

	/**
	 * 
	 * @param addr
	 */
	public IpResponse(InetAddress addr) {
		if (addr != null) {
			ipAddress = addr.getHostAddress();
			version = IpVersion.fromInetAddress(addr);
		}
	}
	
	/**
	 * IP version (IPv4 or IPv6).
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@io.swagger.annotations.ApiModel("IP version (IPv4 or IPv6).")
	@JsonClassDescription("IP version (IPv4 or IPv6).")
	public static enum IpVersion {
		
		/**
		 * 
		 */
		//@Expose
		//@SerializedName("v4")
		//@JsonProperty("v4")
		IPv4,
		
		/**
		 * 
		 */
		//@Expose
		//@SerializedName("v6")
		//@JsonProperty("v6")
		IPv6;
		
		/**
		 * 
		 * @param inetAddress
		 * @return
		 */
		public static IpVersion fromInetAddress(InetAddress addr) {
        	if (addr instanceof Inet4Address) {
    			return IPv4;
    		} else if (addr instanceof Inet6Address) {
    			return IPv6;
    		}
        	
        	return null;
        }
	}
}
