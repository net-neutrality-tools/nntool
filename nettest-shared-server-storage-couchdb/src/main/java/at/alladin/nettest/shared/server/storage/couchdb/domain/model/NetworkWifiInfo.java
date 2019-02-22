package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains WIFI information.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains WIFI information.")
public class NetworkWifiInfo {

    /**
     * Initial SSID of the network.
     */
	@JsonPropertyDescription("Initial SSID of the network.")
	@Expose
    @SerializedName("initial_ssid")
	@JsonProperty("initial_ssid")
    private String initialSsid;
    
    /**
     * Initial BSSID of the network.
     */
	@JsonPropertyDescription("Initial BSSID of the network.")
	@Expose
    @SerializedName("initial_bssid")
	@JsonProperty("initial_bssid")
    private String initialBssid;
	
	public String getInitialSsid() {
		return initialSsid;
	}
	
	public void setInitialSsid(String initialSsid) {
		this.initialSsid = initialSsid;
	}
	
	public String getInitialBssid() {
		return initialBssid;
	}
	
	public void setInitialBssid(String initialBssid) {
		this.initialBssid = initialBssid;
	}
}
