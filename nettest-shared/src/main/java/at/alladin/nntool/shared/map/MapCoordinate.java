package at.alladin.nntool.shared.map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapCoordinate {

    @Expose
    private Double lat;

    @Expose
    private Double lon;

    @Expose
    private Double x;

    @Expose
    private Double y;

    @Expose
    @SerializedName(value = "zoom", alternate = {"z"})
    @JsonProperty("z")
    @JsonAlias("zoom")
    private Integer zoom;

    @Expose
    private Integer size;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "MapCoordinate{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", x=" + x +
                ", y=" + y +
                ", zoom=" + zoom +
                ", size=" + size +
                '}';
    }
}
