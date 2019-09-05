package at.alladin.nettest.service.map.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import at.alladin.nettest.service.map.domain.model.MapServiceOptions;

@ConfigurationProperties(prefix = "map-settings")
public class MapServiceSettingsConfig {

	private List<MapServiceOptions> mapServiceOptions;
	
    private Integer maxZoomLevel;
    
    //TODO: read the signal thresholds from settings
    private List<Double> mobileSignalThresholds;
    
    private List<String> mobileSignalThresholdCaptions;
    
    private List<Double> wifiSignalThresholds;
    
    private List<String> wifiSignalThresholdCaptions;

	public List<MapServiceOptions> getMapServiceOptions() {
		return mapServiceOptions;
	}

	public void setMapServiceOptions(List<MapServiceOptions> mapServiceOptions) {
		this.mapServiceOptions = mapServiceOptions;
	}

	public Integer getMaxZoomLevel() {
		return maxZoomLevel;
	}

	public void setMaxZoomLevel(Integer maxZoomLevel) {
		this.maxZoomLevel = maxZoomLevel;
	}

	public List<Double> getMobileSignalThresholds() {
		return mobileSignalThresholds;
	}

	public void setMobileSignalThresholds(List<Double> mobileSignalThresholds) {
		this.mobileSignalThresholds = mobileSignalThresholds;
	}

	public List<String> getMobileSignalThresholdCaptions() {
		return mobileSignalThresholdCaptions;
	}

	public void setMobileSignalThresholdCaptions(List<String> mobileSignalThresholdCaptions) {
		this.mobileSignalThresholdCaptions = mobileSignalThresholdCaptions;
	}

	public List<Double> getWifiSignalThresholds() {
		return wifiSignalThresholds;
	}

	public void setWifiSignalThresholds(List<Double> wifiSignalThresholds) {
		this.wifiSignalThresholds = wifiSignalThresholds;
	}

	public List<String> getWifiSignalThresholdCaptions() {
		return wifiSignalThresholdCaptions;
	}

	public void setWifiSignalThresholdCaptions(List<String> wifiSignalThresholdCaptions) {
		this.wifiSignalThresholdCaptions = wifiSignalThresholdCaptions;
	}
	
}
