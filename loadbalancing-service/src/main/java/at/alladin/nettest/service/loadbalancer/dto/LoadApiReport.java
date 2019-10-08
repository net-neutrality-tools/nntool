package at.alladin.nettest.service.loadbalancer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class LoadApiReport {

	@JsonProperty("last_response")
	LoadApiResponse lastResponse;
	
	@JsonProperty("last_attempt")
	Long lastAttempt;

	@JsonProperty("last_successful_attempt")
	Long lastSuccessfulAttempt;

	@JsonProperty("fails_since_last_attempt")
	Integer failesSinceLastAttempt;

	public LoadApiResponse getLastResponse() {
		return lastResponse;
	}

	public void setLastResponse(LoadApiResponse lastResponse) {
		this.lastResponse = lastResponse;
	}

	public Long getLastAttempt() {
		return lastAttempt;
	}

	public void setLastAttempt(Long lastAttempt) {
		this.lastAttempt = lastAttempt;
	}

	public Long getLastSuccessfulAttempt() {
		return lastSuccessfulAttempt;
	}

	public void setLastSuccessfulAttempt(Long lastSuccessfulAttempt) {
		this.lastSuccessfulAttempt = lastSuccessfulAttempt;
	}

	public Integer getFailesSinceLastAttempt() {
		return failesSinceLastAttempt;
	}

	public void setFailesSinceLastAttempt(Integer failesSinceLastAttempt) {
		this.failesSinceLastAttempt = failesSinceLastAttempt;
	}
}
