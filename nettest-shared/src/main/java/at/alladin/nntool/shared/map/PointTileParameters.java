package at.alladin.nntool.shared.map;

public class PointTileParameters extends MapTileParameters {

    /**
     *
     */
    protected Double pointDiameter;

    /**
     *
     */
    protected Boolean noFill;

    /**
     *
     */
    protected Boolean noColor;

    /**
     *
     */
    protected String highlight;

    @Override
    public boolean isNoCache() {
        return highlight != null && highlight.length() > 0; //TODO: different caching for null and ""
    }

    @Override
    public MapTileType getMapTileType() {
        return MapTileType.POINTS;
    }

    public Double getPointDiameter() {
        return pointDiameter;
    }

    public void setPointDiameter(Double pointDiameter) {
        this.pointDiameter = pointDiameter;
    }

    public Boolean isNoFill() {
        return noFill;
    }

    public void setNoFill(Boolean noFill) {
        this.noFill = noFill;
    }

    public Boolean isNoColor() {
        return noColor;
    }

    public void setNoColor(Boolean noColor) {
        this.noColor = noColor;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }
}
