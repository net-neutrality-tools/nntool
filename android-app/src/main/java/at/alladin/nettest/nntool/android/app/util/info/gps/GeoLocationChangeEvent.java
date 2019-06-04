package at.alladin.nettest.nntool.android.app.util.info.gps;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class GeoLocationChangeEvent {
    private GeoLocationDto geoLocationDto;

    public GeoLocationChangeEvent(final GeoLocationDto geoLocationDto) {
        this.geoLocationDto = geoLocationDto;
    }

    public GeoLocationDto getGeoLocationDto() {
        return geoLocationDto;
    }

    public void setGeoLocationDto(GeoLocationDto geoLocationDto) {
        this.geoLocationDto = geoLocationDto;
    }

    @Override
    public String toString() {
        return "GeoLocationChangeEvent{" +
                "geoLocationDto=" + geoLocationDto +
                '}';
    }
}
