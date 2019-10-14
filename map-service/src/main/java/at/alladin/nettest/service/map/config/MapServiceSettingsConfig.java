package at.alladin.nettest.service.map.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import at.alladin.nettest.service.map.domain.model.MapServiceOptions;

@ConfigurationProperties(prefix = "map-settings")
public class MapServiceSettingsConfig {

	private List<MapServiceOptions> mapServiceOptions;
	
    private Integer maxZoomLevel;

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
    
}
