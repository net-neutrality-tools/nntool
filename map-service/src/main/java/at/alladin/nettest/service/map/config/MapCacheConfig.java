package at.alladin.nettest.service.map.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="map-cache")
public class MapCacheConfig {

    private Boolean usePointTileCache;

    private Boolean useHeatmapTileCache;

    private Boolean useShapeTileCache;
    
    private Integer cacheIgnoreZoomLevel;

    private Integer cacheStaleSeconds;

    private Integer cacheExpireSeconds;

    public Boolean getUsePointTileCache() {
        return usePointTileCache;
    }

    public void setUsePointTileCache(Boolean usePointTileCache) {
        this.usePointTileCache = usePointTileCache;
    }

    public Boolean getUseHeatmapTileCache() {
        return useHeatmapTileCache;
    }

    public void setUseHeatmapTileCache(Boolean useHeatmapTileCache) {
        this.useHeatmapTileCache = useHeatmapTileCache;
    }

    public Boolean getUseShapeTileCache() {
        return useShapeTileCache;
    }

    public void setUseShapeTileCache(Boolean useShapeTileCache) {
        this.useShapeTileCache = useShapeTileCache;
    }

	public Integer getCacheIgnoreZoomLevel() {
		return cacheIgnoreZoomLevel;
	}

	public void setCacheIgnoreZoomLevel(Integer cacheIgnoreZoomLevel) {
		this.cacheIgnoreZoomLevel = cacheIgnoreZoomLevel;
	}

	public Integer getCacheStaleSeconds() {
		return cacheStaleSeconds;
	}

	public void setCacheStaleSeconds(Integer cacheStaleSeconds) {
		this.cacheStaleSeconds = cacheStaleSeconds;
	}

	public Integer getCacheExpireSeconds() {
		return cacheExpireSeconds;
	}

	public void setCacheExpireSeconds(Integer cacheExpireSeconds) {
		this.cacheExpireSeconds = cacheExpireSeconds;
	}
    
}
