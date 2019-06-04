package at.alladin.nettest.nntool.android.app.util.info.gps;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class GeoLocationChangeEvent {
    public static enum GeoLocationChangeEventType {
        DISABLED,
        ENABLED,
        LOCATION_UPDATE
    }

    private GeoLocationDto geoLocationDto;

    private GeoLocationChangeEventType eventType;

    public GeoLocationChangeEvent(final GeoLocationDto geoLocationDto, final GeoLocationChangeEventType type) {
        this.geoLocationDto = geoLocationDto;
        this.eventType = type;
    }

    public GeoLocationDto getGeoLocationDto() {
        return geoLocationDto;
    }

    public void setGeoLocationDto(GeoLocationDto geoLocationDto) {
        this.geoLocationDto = geoLocationDto;
    }

    public GeoLocationChangeEventType getEventType() {
        return eventType;
    }

    public void setEventType(GeoLocationChangeEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "GeoLocationChangeEvent{" +
                "geoLocationDto=" + geoLocationDto +
                ", eventType=" + eventType +
                '}';
    }
}
