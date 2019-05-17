package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the QoS measurement.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the QoS measurement.")
public class QoSMeasurement extends SubMeasurement {

	/**
	 * Provides detailed information on all QoS results.
	 */
	@JsonPropertyDescription("Provides detailed information on all QoS results.")
	@Expose
	@SerializedName("results")
	@JsonProperty("results")
	private List<QoSResult> results;
	
	public List<QoSResult> getResults() {
		return results;
	}
	
	public void setResults(List<QoSResult> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "QoSMeasurement [results=" + results + "]";
	}
	
}