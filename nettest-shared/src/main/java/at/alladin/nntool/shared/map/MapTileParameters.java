package at.alladin.nntool.shared.map;

import java.util.List;
import java.util.Map;

public abstract class MapTileParameters {
    /**
     *
     */
//    protected static final int MAX_ZOOM = 21;

    protected Integer zoom;

    protected Integer x;

    protected Integer y;

    protected Integer size;
    
    protected Integer period;
    
    protected String technology;
    
    protected String provider;
    
    protected String operator;

    protected String mapOption = "mobile/download"; //default value

    protected Float quantile;
    
    protected String highlight;
    
    protected String highlightUuid; 
    
    protected String clientUuid;

    protected List<String> filterList;

    /**
     * Contains the key and the value of a to-be-applied filter
     * e.g. "period", "180" or "provider", "other"
     */
    protected Map<String, String> filterMap;

    protected Double transparency;

    /**
     *
     * @return whether the given parameters shall be cached
     */
    public abstract boolean isNoCache();

    /**
     *
     * @return the type of the given parameter object
     */
    public abstract MapTileType getMapTileType();

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getMapOption() {
        return mapOption;
    }

    public void setMapOption(String mapOption) {
        this.mapOption = mapOption;
    }

    public Float getQuantile() {
        return quantile;
    }

    public void setQuantile(Float quantile) {
        this.quantile = quantile;
    }

    public List<String> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<String> filterList) {
        this.filterList = filterList;
    }

    public Map<String, String> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<String, String> filterMap) {
        this.filterMap = filterMap;
    }

    public Double getTransparency() {
        return transparency;
    }

    public void setTransparency(Double transparency) {
        this.transparency = transparency;
    }

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

	public String getHighlightUuid() {
		return highlightUuid;
	}

	public void setHighlightUuid(String highlightUuid) {
		this.highlightUuid = highlightUuid;
	}

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}
	
}
