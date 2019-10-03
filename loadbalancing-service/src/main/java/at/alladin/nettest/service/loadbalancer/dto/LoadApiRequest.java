package at.alladin.nettest.service.loadbalancer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class LoadApiRequest {

	@JsonProperty("cmd")
	final String cmd = "load";
	
	@JsonProperty("secret")
	String secret;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getCmd() {
		return cmd;
	}

	@Override
	public String toString() {
		return "LoadApiRequest [cmd=" + cmd + ", secret=" + secret + "]";
	}
}
