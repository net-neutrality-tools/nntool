package at.alladin.nettest.service.statistic.web.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
@io.swagger.annotations.ApiModel(description = "Holds statistical information of a single provider.")
@JsonClassDescription("Holds statistical information of a single provider.")
public class ProviderStatisticDto {

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The name of the provider.")
    @JsonPropertyDescription("The name of the provider.")
    @Expose
    @SerializedName("provider_name")
    @JsonProperty(required = true, value = "provider_name")
    private String providerName;

    @io.swagger.annotations.ApiModelProperty(required = false, value = "The ASN of the provider.")
    @JsonPropertyDescription("The ASN of the provider.")
    @Expose
    @SerializedName("provider_asn")
    @JsonProperty(required = true, value = "provider_asn")
    private Integer providerAsn;
    
    @io.swagger.annotations.ApiModelProperty(required = true, value = "The download (percentile) of this provider in bps.")
    @JsonPropertyDescription("The download (percentile) of this provider in bps.")
    @Expose
    @SerializedName("download_bps")
    @JsonProperty(required = true, value = "download_bps")
    private Long downloadBps;

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The upload (percentile) of this provider in bps.")
    @JsonPropertyDescription("The upload (percentile) of this provider in bps.")
    @Expose
    @SerializedName("upload_bps")
    @JsonProperty(required = true, value = "upload_bps")
    private Long uploadBps;

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The RTT (percentile) of this provider in nanoseconds.")
    @JsonPropertyDescription("The RTT (percentile) of this provider in nanoseconds.")
    @Expose
    @SerializedName("rtt_ns")
    @JsonProperty(required = true, value = "rtt_ns")
    private Long rttNs;

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The total count of measurements.")
    @JsonPropertyDescription("The total count of measurements.")
    @Expose
    @SerializedName("count")
    @JsonProperty(required = true, value = "count")
    private Long count;
    
    @io.swagger.annotations.ApiModelProperty(required = false, value = "The MCC/MNC of the provider.")
    @JsonPropertyDescription("The MCC/MNC of the provider.")
    @Expose
    @SerializedName("mcc_mnc")
    @JsonProperty(required = true, value = "mcc_mnc")
    private String mccMnc;
    
    @io.swagger.annotations.ApiModelProperty(required = false, value = "The country code of the provider.")
    @JsonPropertyDescription("The country code of the provider.")
    @Expose
    @SerializedName("country_code")
    @JsonProperty(required = true, value = "country_code")
    private String countryCode;

    @io.swagger.annotations.ApiModelProperty(required = false, value = "The signal strength (percentile) of this provider.")
    @JsonPropertyDescription("The signal strength (percentile) of this provider.")
    @Expose
    @SerializedName("signal_strength_dbm")
    @JsonProperty(required = true, value = "signal_strength_dbm")
    private Integer signalStrengthDbm;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Integer getProviderAsn() {
		return providerAsn;
	}

	public void setProviderAsn(Integer providerAsn) {
		this.providerAsn = providerAsn;
	}
    
    public Long getDownloadBps() {
		return downloadBps;
	}
    
    public void setDownloadBps(Long downloadBps) {
		this.downloadBps = downloadBps;
	}
    
    public Long getUploadBps() {
		return uploadBps;
	}
    
    public void setUploadBps(Long uploadBps) {
		this.uploadBps = uploadBps;
	}

    public Long getRttNs() {
        return rttNs;
    }

    public void setRttNs(Long rttNs) {
        this.rttNs = rttNs;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

	public String getMccMnc() {
		return mccMnc;
	}

	public void setMccMnc(String mccMnc) {
		this.mccMnc = mccMnc;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getSignalStrengthDbm() {
		return signalStrengthDbm;
	}

	public void setSignalStrengthDbm(Integer signalStrengthDbm) {
		this.signalStrengthDbm = signalStrengthDbm;
	}

	@Override
	public String toString() {
		return "ProviderStatisticDto [providerName=" + providerName + ", downloadMedBps=" + downloadBps + ", uploadMedBps=" + uploadBps + ", rttNs="
				+ rttNs + ", count=" + count + ", mccMnc=" + mccMnc + ", asn="
				+ providerAsn + ", countryCode=" + countryCode + ", signalStrengthDbm=" + signalStrengthDbm + "]";
	}
}
