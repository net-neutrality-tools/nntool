/*******************************************************************************
 * Copyright 2016-2019 alladin-IT GmbH
 * Copyright 2016 SPECURE GmbH
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

package at.alladin.nettest.shared.model;

import javax.annotation.Generated;

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb
 *
 */
@Generated("org.jsonschema2pojo")
public class Signal {

    /**
     * TODO:
     * New field (results only); referenced by using network_info.network_type (references table: [network_type] column: [name])
     * 
     * (Required)
     */
    @SerializedName("network_type")
    @Expose
    private String networkType;
    
    /**
     * TODO:
     * New field (results only); referenced by using network_info.network_type (references table: [network_type] column: [group_name])
     * 
     * (Required)
     */
    @SerializedName("cat_technology")
    @Expose
    private String catTechnology; //rem
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("time")
    @Expose
    private DateTime time;
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("signal_strength")
    @Expose
    private Integer signalStrength; //rename signalStrength2g3gDbm
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("wifi_link_speed")
    @Expose
    private Integer wifiLinkSpeed; //rename wifiLinkSpeedMbps
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("gsm_bit_error_rate")
    @Expose
    private Integer gsmBitErrorRate;
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("wifi_rssi")
    @Expose
    private Integer wifiRssi; //rename wifiRssiDbm
    
    /**
     * TODO:
     * 
     * Renamed from time_ns
     * 
     * (Required)
     */
    @SerializedName("relative_time_ns")
    @Expose
    private Long relativeTimeNs;
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("lte_rsrp")
    @Expose
    private Integer lteRsrp; //rename lteRsrpDbm
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("lte_rsrq")
    @Expose
    private Integer lteRsrq; //rename lteRsrqDb
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("lte_rssnr")
    @Expose
    private Integer lteRssnr; //rename lteRssnrDb
    
    /**
     * TODO:
     * 
     * (Required)
     */
    @SerializedName("lte_cqi")
    @Expose
    private Integer lteCqi;

    /**
     * TODO:
     */
	@SerializedName("network_type_id")
	@Expose
	private Integer networkTypeId; //mv new class //(something like NetworkType) 

	
	public Integer getNetworkTypeId() {
		return networkTypeId;
	}

	public void setNetworkTypeId(Integer networkTypeId) {
		this.networkTypeId = networkTypeId;
	}
	
    /**
     * new field (results only); referenced by using network_info.network_type (references table: [network_type] column: [name])
     * (Required)
     * 
     * @return
     *     The networkType
     */
    public String getNetworkType() {
        return networkType;
    }

    /**
     * new field (results only); referenced by using network_info.network_type (references table: [network_type] column: [name])
     * (Required)
     * 
     * @param networkType
     *     The network_type
     */
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    /**
     * new field (results only); referenced by using network_info.network_type (references table: [network_type] column: [group_name])
     * (Required)
     * 
     * @return
     *     The catTechnology
     */
    public String getCatTechnology() {
        return catTechnology;
    }

    /**
     * new field (results only); referenced by using network_info.network_type (references table: [network_type] column: [group_name])
     * (Required)
     * 
     * @param catTechnology
     *     The cat_technology
     */
    public void setCatTechnology(String catTechnology) {
        this.catTechnology = catTechnology;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The time
     */
    public DateTime getTime() {
        return time;
    }

    /**
     * 
     * (Required)
     * 
     * @param time
     *     The time
     */
    public void setTime(DateTime time) {
        this.time = time;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The signalStrength
     */
    public Integer getSignalStrength() {
        return signalStrength;
    }

    /**
     * 
     * (Required)
     * 
     * @param signalStrength
     *     The signal_strength
     */
    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The wifiLinkSpeed
     */
    public Integer getWifiLinkSpeed() {
        return wifiLinkSpeed;
    }

    /**
     * 
     * (Required)
     * 
     * @param wifiLinkSpeed
     *     The wifi_link_speed
     */
    public void setWifiLinkSpeed(Integer wifiLinkSpeed) {
        this.wifiLinkSpeed = wifiLinkSpeed;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The gsmBitErrorRate
     */
    public Integer getGsmBitErrorRate() {
        return gsmBitErrorRate;
    }

    /**
     * 
     * (Required)
     * 
     * @param gsmBitErrorRate
     *     The gsm_bit_error_rate
     */
    public void setGsmBitErrorRate(Integer gsmBitErrorRate) {
        this.gsmBitErrorRate = gsmBitErrorRate;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The wifiRssi
     */
    public Integer getWifiRssi() {
        return wifiRssi;
    }

    /**
     * 
     * (Required)
     * 
     * @param wifiRssi
     *     The wifi_rssi
     */
    public void setWifiRssi(Integer wifiRssi) {
        this.wifiRssi = wifiRssi;
    }

    /**
     * renamed from time_ns
     * (Required)
     * 
     * @return
     *     The relativeTimeNs
     */
    public Long getRelativeTimeNs() {
        return relativeTimeNs;
    }

    /**
     * renamed from time_ns
     * (Required)
     * 
     * @param relativeTimeNs
     *     The relative_time_ns
     */
    public void setRelativeTimeNs(Long relativeTimeNs) {
        this.relativeTimeNs = relativeTimeNs;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The lteRsrp
     */
    public Integer getLteRsrp() {
        return lteRsrp;
    }

    /**
     * 
     * (Required)
     * 
     * @param lteRsrp
     *     The lte_rsrp
     */
    public void setLteRsrp(Integer lteRsrp) {
        this.lteRsrp = lteRsrp;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The lteRsrq
     */
    public Integer getLteRsrq() {
        return lteRsrq;
    }

    /**
     * 
     * (Required)
     * 
     * @param lteRsrq
     *     The lte_rsrq
     */
    public void setLteRsrq(Integer lteRsrq) {
        this.lteRsrq = lteRsrq;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The lteRssnr
     */
    public Integer getLteRssnr() {
        return lteRssnr;
    }

    /**
     * 
     * (Required)
     * 
     * @param lteRssnr
     *     The lte_rssnr
     */
    public void setLteRssnr(Integer lteRssnr) {
        this.lteRssnr = lteRssnr;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The lteCqi
     */
    public Integer getLteCqi() {
        return lteCqi;
    }

    /**
     * 
     * (Required)
     * 
     * @param lteCqi
     *     The lte_cqi
     */
    public void setLteCqi(Integer lteCqi) {
        this.lteCqi = lteCqi;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catTechnology == null) ? 0 : catTechnology.hashCode());
		result = prime * result + ((gsmBitErrorRate == null) ? 0 : gsmBitErrorRate.hashCode());
		result = prime * result + ((lteCqi == null) ? 0 : lteCqi.hashCode());
		result = prime * result + ((lteRsrp == null) ? 0 : lteRsrp.hashCode());
		result = prime * result + ((lteRsrq == null) ? 0 : lteRsrq.hashCode());
		result = prime * result + ((lteRssnr == null) ? 0 : lteRssnr.hashCode());
		result = prime * result + ((networkType == null) ? 0 : networkType.hashCode());
		result = prime * result + ((relativeTimeNs == null) ? 0 : relativeTimeNs.hashCode());
		result = prime * result + ((signalStrength == null) ? 0 : signalStrength.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((wifiLinkSpeed == null) ? 0 : wifiLinkSpeed.hashCode());
		result = prime * result + ((wifiRssi == null) ? 0 : wifiRssi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Signal other = (Signal) obj;
		if (catTechnology == null) {
			if (other.catTechnology != null)
				return false;
		} else if (!catTechnology.equals(other.catTechnology))
			return false;
		if (gsmBitErrorRate == null) {
			if (other.gsmBitErrorRate != null)
				return false;
		} else if (!gsmBitErrorRate.equals(other.gsmBitErrorRate))
			return false;
		if (lteCqi == null) {
			if (other.lteCqi != null)
				return false;
		} else if (!lteCqi.equals(other.lteCqi))
			return false;
		if (lteRsrp == null) {
			if (other.lteRsrp != null)
				return false;
		} else if (!lteRsrp.equals(other.lteRsrp))
			return false;
		if (lteRsrq == null) {
			if (other.lteRsrq != null)
				return false;
		} else if (!lteRsrq.equals(other.lteRsrq))
			return false;
		if (lteRssnr == null) {
			if (other.lteRssnr != null)
				return false;
		} else if (!lteRssnr.equals(other.lteRssnr))
			return false;
		if (networkType == null) {
			if (other.networkType != null)
				return false;
		} else if (!networkType.equals(other.networkType))
			return false;
		if (relativeTimeNs == null) {
			if (other.relativeTimeNs != null)
				return false;
		} else if (!relativeTimeNs.equals(other.relativeTimeNs))
			return false;
		if (signalStrength == null) {
			if (other.signalStrength != null)
				return false;
		} else if (!signalStrength.equals(other.signalStrength))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (wifiLinkSpeed == null) {
			if (other.wifiLinkSpeed != null)
				return false;
		} else if (!wifiLinkSpeed.equals(other.wifiLinkSpeed))
			return false;
		if (wifiRssi == null) {
			if (other.wifiRssi != null)
				return false;
		} else if (!wifiRssi.equals(other.wifiRssi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MeasurementSignal [networkType=" + networkType + ", catTechnology=" + catTechnology + ", time=" + time
				+ ", signalStrength=" + signalStrength + ", wifiLinkSpeed=" + wifiLinkSpeed + ", gsmBitErrorRate="
				+ gsmBitErrorRate + ", wifiRssi=" + wifiRssi + ", relativeTimeNs=" + relativeTimeNs + ", lteRsrp="
				+ lteRsrp + ", lteRsrq=" + lteRsrq + ", lteRssnr=" + lteRssnr + ", lteCqi=" + lteCqi + "]";
	}
}