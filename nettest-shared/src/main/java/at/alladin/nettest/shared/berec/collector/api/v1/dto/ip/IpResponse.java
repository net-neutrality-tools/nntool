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
 * Response object sent to the measurement agent after a successful IP request.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Response object sent to the measurement agent after a successful IP request.")
@JsonClassDescription("Response object sent to the measurement agent after a successful IP request.")
public class IpResponse {

	/**
	 * The measurement agent's public IP address.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The measurement agent's public IP address.")
	@JsonPropertyDescription("The measurement agent's public IP address.")
	@Expose
	@SerializedName("ip_address")
	@JsonProperty(required = true, value = "ip_address")
	private String ipAddress;
	
	/**
	 * The measurement agent's public IP version (IPv4 or IPv6).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The measurement agent's public IP version (IPv4 or IPv6).")
	@JsonPropertyDescription("The measurement agent's public IP version (IPv4 or IPv6).")
	@Expose
	@SerializedName("ip_version")
	@JsonProperty(required = true, value = "ip_version")
	private IpVersion version; // TODO: rename ipVersion

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
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public IpVersion getVersion() {
		return version;
	}

	public void setVersion(IpVersion version) {
		this.version = version;
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
