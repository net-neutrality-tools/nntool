package at.alladin.nettest.service.statistic.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
@io.swagger.annotations.ApiModel(description = "Holds statistical information of a single provider")
@JsonClassDescription("Holds statistical information of a single provider")
public class ProviderStatisticDto {

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The name of the provider")
    @JsonPropertyDescription("The name of the provider.")
    @Expose
    @SerializedName("name")
    @JsonProperty(required = true, value = "name")
    private String name;

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The download (median) of this provider in Kbps.")
    @JsonPropertyDescription("The download (median) of this provider in Kbps.")
    @Expose
    @SerializedName("down_kbps")
    @JsonProperty(required = true, value = "down_kbps")
    private Long downKbps;

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The upload (median) of this provider in Kbps.")
    @JsonPropertyDescription("The upload (median) of this provider in Kbps.")
    @Expose
    @SerializedName("up_kbps")
    @JsonProperty(required = true, value = "up_kbps")
    private Long upKbps;

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The RTT (median) of this provider in milliseconds.")
    @JsonPropertyDescription("The RTT (median) of this provider in milliseconds.")
    @Expose
    @SerializedName("rtt_ms")
    @JsonProperty(required = true, value = "rtt_ms")
    private Long rttMs;

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The signal strength (median).")
    @JsonPropertyDescription("The signal strength (median).")
    @Expose
    @SerializedName("signal_strength")
    @JsonProperty(required = true, value = "signal_strength")
    private Long signalStrength;

    @io.swagger.annotations.ApiModelProperty(required = true, value = "The total amount of measurements.")
    @JsonPropertyDescription("The total amount of measurements.")
    @Expose
    @SerializedName("amount")
    @JsonProperty(required = true, value = "amount")
    private Long amount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDownKbps() {
        return downKbps;
    }

    public void setDownKbps(Long downKbps) {
        this.downKbps = downKbps;
    }

    public Long getUpKbps() {
        return upKbps;
    }

    public void setUpKbps(Long upKbps) {
        this.upKbps = upKbps;
    }

    public Long getRttMs() {
        return rttMs;
    }

    public void setRttMs(Long rttMs) {
        this.rttMs = rttMs;
    }

    public Long getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Long signalStrength) {
        this.signalStrength = signalStrength;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
