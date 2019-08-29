/*******************************************************************************
 * Copyright 2017-2019 alladin-IT GmbH
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

package at.alladin.nettest.shared.model.qos;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nntool.shared.qos.AbstractResult;
import at.alladin.nntool.shared.qos.DnsResult;
import at.alladin.nntool.shared.qos.EchoProtocolResult;
import at.alladin.nntool.shared.qos.HttpProxyResult;
import at.alladin.nntool.shared.qos.NonTransparentProxyResult;
import at.alladin.nntool.shared.qos.SipResult;
import at.alladin.nntool.shared.qos.TcpResult;
import at.alladin.nntool.shared.qos.TracerouteResult;
import at.alladin.nntool.shared.qos.UdpResult;
import at.alladin.nntool.shared.qos.VoipResult;
import at.alladin.nntool.shared.qos.WebsiteResult;

/**
 * The specific QoS type
 * 
 * @author bp
 *
 */
public enum QosMeasurementType {

	/**
	 * 
	 */
    @SerializedName("tcp")
    TCP("tcp", "test.tcp", "name.tcp", TcpResult.class),
    
    /**
     * 
     */
    @SerializedName("udp")
    UDP("udp", "test.udp", "name.udp", UdpResult.class),
    
    /**
	 * 
	 */
    @SerializedName("dns")
    DNS("dns", "test.dns", "name.dns", DnsResult.class),
    
    /**
	 * 
	 */
    @SerializedName("non_transparent_proxy")
    NON_TRANSPARENT_PROXY("non_transparent_proxy", "test.ntp", "name.non_transparent_proxy", NonTransparentProxyResult.class),
    
    /**
	 * 
	 */
    @SerializedName("http_proxy")
    HTTP_PROXY("http_proxy", "test.http", "name.http_proxy", HttpProxyResult.class),
    
    /**
	 * 
	 */
    @SerializedName("voip")
    VOIP("voip", "test.voip", "name.voip", VoipResult.class),
    
    /**
	 * 
	 */
    @SerializedName("traceroute")
    TRACEROUTE("traceroute", "test.trace", "name.trace", TracerouteResult.class),
    
    /**
	 * 
	 */
    @SerializedName("website")
    WEBSITE("website", "test.website", "name.website", WebsiteResult.class),

    /**
     * 
     */
    @SerializedName("echo_protocol")
    ECHO_PROTOCOL("echo_protocol", "test.echo", "name.echo", EchoProtocolResult.class),
    
    /**
     * 
     */
    @SerializedName("sip")
    SIP("sip", "test.sip", "name.sip", SipResult.class);

    /**
     * 
     */
    private final String value;
    
    /**
     * 
     */
    private final String descriptionKey;
    
    /**
     * 
     */
    private final String nameKey;
    
    /**
     * 
     */
    private final Class<? extends AbstractResult> resultClass;
    
    /**
     * 
     */
    public static final Map<String, QosMeasurementType> CONSTANTS = new HashMap<>();

    /**
     * 
     */
    static {
        for (QosMeasurementType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    /**
     * 
     * @param value
     */
    private QosMeasurementType(String value, String descriptionKey, String nameKey, Class<? extends AbstractResult> resultClass) {
        this.value = value;
        this.descriptionKey = descriptionKey;
        this.nameKey = nameKey;
        this.resultClass = resultClass;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * 
     * @return
     */
    public String getValue() {
		return value;
	}
    
    /**
     * 
     * @return
     */
    public String getDescriptionKey() {
		return descriptionKey;
	}
    
    /**
     * 
     * @return
     */
    public String getNameKey() {
		return nameKey;
	}
    
    /**
     * 
     * @return
     */
    public Class<? extends AbstractResult> getResultClass() {
		return resultClass;
	}
    
    /**
     * 
     * @param value
     * @return
     */
    public static QosMeasurementType fromValue(String value) {
    	final QosMeasurementType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

    public static QosMeasurementType fromQosTypeDto(final QoSMeasurementTypeDto dto) {
        switch(dto) {
            case DNS:
                return DNS;
            case TCP:
                return TCP;
            case UDP:
                return UDP;
            case VOIP:
                return VOIP;
            case WEBSITE:
                return WEBSITE;
            case HTTP_PROXY:
                return HTTP_PROXY;
            case ECHO_PROTOCOL:
                return ECHO_PROTOCOL;
            case NON_TRANSPARENT_PROXY:
                return NON_TRANSPARENT_PROXY;
            case TRACEROUTE:
                return TRACEROUTE;
            case SIP:
                return SIP;
            default:
                return null;
        }
    }
}
