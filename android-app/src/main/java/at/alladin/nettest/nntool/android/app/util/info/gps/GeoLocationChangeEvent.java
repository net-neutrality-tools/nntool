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
