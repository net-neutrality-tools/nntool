/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
