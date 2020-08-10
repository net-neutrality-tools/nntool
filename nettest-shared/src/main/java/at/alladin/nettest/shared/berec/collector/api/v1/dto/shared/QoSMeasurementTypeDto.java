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

package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * Enum that holds the names of the available QoS measurement objectives.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Enum that holds the names of the available QoS measurement objectives.")
@JsonClassDescription("Enum that holds the names of the available QoS measurement objectives.")
public enum QoSMeasurementTypeDto {
    TCP,
    UDP,
    UDP_TURN,
    DNS,
    NON_TRANSPARENT_PROXY,
    HTTP_PROXY,
    VOIP,
    TRACEROUTE,
    WEBSITE,
    ECHO_PROTOCOL,
    SIP,
    AUDIO_STREAMING,

    @JsonEnumDefaultValue
    UNSUPPORTED,

    MKIT_DASH,
    MKIT_WEB_CONNECTIVITY;
}
