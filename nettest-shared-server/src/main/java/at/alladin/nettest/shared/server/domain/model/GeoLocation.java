package at.alladin.nettest.shared.server.domain.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Geographic location information from a point in time.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Geographic location information from a point in time.")
public class GeoLocation {

    /**
     * Time and date the geographic location information was captured (UTC).
     */
	@JsonPropertyDescription("Time and date the geographic location information was captured (UTC).")
	@Expose
    @SerializedName("time")
    @JsonProperty("time")
    private LocalDateTime time;
    
    /**
     * Geographic location accuracy.
     */
	@JsonPropertyDescription("Geographic location accuracy.")
    @Expose
    @SerializedName("accuracy")
    @JsonProperty("accuracy")
    private Double accuracy;

    /**
     * Geographic location altitude.
     */
	@JsonPropertyDescription("Geographic location altitude.")
    @Expose
    @SerializedName("altitude")
    @JsonProperty("altitude")
    private Double altitude;

    /**
     * Movement heading.
     */
	@JsonPropertyDescription("Movement heading.")
    @Expose
    @SerializedName("heading")
    @JsonProperty("heading")
    private Double heading;

    /**
     * Movement speed.
     */
	@JsonPropertyDescription("Movement speed.")
    @Expose
    @SerializedName("speed")
    @JsonProperty("speed")
    private Double speed;

    /**
     * Geographic location provider.
     */
	@JsonPropertyDescription("Geographic location provider.")
    @Expose
    @SerializedName("provider")
    @JsonProperty("provider")
    private String provider;
    
    /**
     * Geographic location latitude.
     */
	@JsonPropertyDescription("Geographic location latitude.")
    @Expose
    @SerializedName("latitude")
    @JsonProperty("latitude")
    private Double latitude;
    
    /**
     * Geographic location longitude.
     */
	@JsonPropertyDescription("Geographic location longitude.")
    @Expose
    @SerializedName("longitude")
    @JsonProperty("longitude")
    private Double longitude;
    
    /**
     * Relative time in nanoseconds (to measurement begin).
     */
	@JsonPropertyDescription("Relative time in nanoseconds (to measurement begin).")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty("relative_time_ns")
    private Long relativeTimeNs;

}