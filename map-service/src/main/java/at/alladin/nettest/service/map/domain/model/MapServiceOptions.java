package at.alladin.nettest.service.map.domain.model;

import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.List;

import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationType;


public class MapServiceOptions {
	
	@Expose
	private String groupKey;
	
	@Expose
	private String typeKey;

    @Expose
    private String sqlValueColumn;

    @Expose
    private String sqlValueColumnLog;

    @Expose
    private String sqlFilter;

    @Expose
    private ClassificationType classificationType;

    @Expose
    private String overlayType;

    @Expose
    private boolean reverseScale;

    @Expose
    private SignalGroup signalGroup;

    public String getSqlValueColumn() {
        return sqlValueColumn;
    }

    public void setSqlValueColumn(String sqlValueColumn) {
        this.sqlValueColumn = sqlValueColumn;
    }

    public String getSqlValueColumnLog() {
        return sqlValueColumnLog;
    }

    public void setSqlValueColumnLog(String sqlValueColumnLog) {
        this.sqlValueColumnLog = sqlValueColumnLog;
    }

    public String getSqlFilter() {
        return sqlFilter;
    }

    public void setSqlFilter(String sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

    public ClassificationType getClassificationType() {
        return classificationType;
    }

    public void setClassificationType(ClassificationType classificationType) {
        this.classificationType = classificationType;
    }

    public String getOverlayType() {
        return overlayType;
    }

    public void setOverlayType(String overlayType) {
        this.overlayType = overlayType;
    }

    public boolean isReverseScale() {
        return reverseScale;
    }

    public void setReverseScale(boolean reverseScale) {
        this.reverseScale = reverseScale;
    }

    public SignalGroup getSignalGroup() {
        return signalGroup;
    }

    public void setSignalGroup(SignalGroup signalGroup) {
        this.signalGroup = signalGroup;
    }

    public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

	public String getTypeKey() {
		return typeKey;
	}

	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}


    @Override
	public String toString() {
		return "MapServiceOptions [groupKey=" + groupKey + ", typeKey=" + typeKey + ", sqlValueColumn=" + sqlValueColumn
				+ ", sqlValueColumnLog=" + sqlValueColumnLog + ", sqlFilter=" + sqlFilter + ", colorsSorted="
				+ ", classificationType=" + classificationType + ", overlayType=" + overlayType
				+ ", reverseScale=" + reverseScale + ", signalGroup=" + signalGroup + "]";
	}

	/**
     * Enum to differentiate between the coarse filtering options of the map server
     * (this does NOT differentiate between 2G, 3G, etc., look at the technology in the parameters for that)
     * @author fk
     *
     */
    public enum SignalGroup {
        MOBILE,
        WIFI,
        BROWSER,
        ALL
    }
}
