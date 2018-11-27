package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Cell location information from a point in time on the measurement agent.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Cell location information from a point in time on the measurement agent.")
@JsonClassDescription("Cell location information from a point in time on the measurement agent.")
public class CellLocationDto {

	/**
	 * Contains the cell-ID, if available.
	 */
	@io.swagger.annotations.ApiModelProperty("Contains the cell-ID, if available.")
	@JsonPropertyDescription("Contains the cell-ID, if available.")
	@Expose
	@SerializedName("cell_id")
	@JsonProperty("cell_id")
	private Integer cellId;

	/**
	 * Contains the area code (e.g. location area code (GSM), tracking area code (LTE)), if available.
	 */
	@io.swagger.annotations.ApiModelProperty("Contains the area code (e.g. location area code (GSM), tracking area code (LTE)), if available.")
	@JsonPropertyDescription("Contains the area code (e.g. location area code (GSM), tracking area code (LTE)), if available.")
	@Expose
	@SerializedName("area_code")
	@JsonProperty("area_code")
	private Integer areaCode;

	/**
	 * Time and date the cell location information was captured (UTC).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Time and date the cell location information was captured (UTC).")
	@JsonPropertyDescription("Time and date the cell location information was captured (UTC).")
	@Expose
	@SerializedName("time")
	@JsonProperty(required = true, value = "time")
	private LocalDateTime time;

	/**
	 * Contains the primary scrambling code, if available.
	 */
	@io.swagger.annotations.ApiModelProperty("Contains the primary scrambling code, if available.")
	@JsonPropertyDescription("Contains the primary scrambling code, if available.")
	@Expose
	@SerializedName("primary_scrambling_code")
	@JsonProperty("primary_scrambling_code")
	private Integer primaryScramblingCode;

	/**
	 * Contains the ARFCN (Absolute Radio Frequency Channel Number) (e.g. 16-bit GSM ARFCN or 18-bit LTE EARFCN), if available.
	 */
	@io.swagger.annotations.ApiModelProperty("Contains the ARFCN (Absolute Radio Frequency Channel Number) (e.g. 16-bit GSM ARFCN or 18-bit LTE EARFCN), if available.")
	@JsonPropertyDescription("Contains the ARFCN (Absolute Radio Frequency Channel Number) (e.g. 16-bit GSM ARFCN or 18-bit LTE EARFCN), if available.")
	@Expose
	@SerializedName("arfcn")
	@JsonProperty("arfcn")
	private Integer arfcn;

	/**
	 * Relative time in nanoseconds (to measurement begin).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Relative time in nanoseconds (to test begin).")
	@JsonPropertyDescription("Relative time in nanoseconds (to test begin).")
	@Expose
	@SerializedName("relative_time_ns")
	@JsonProperty(required = true, value = "relative_time_ns")
	private Long relativeTimeNs;

    /**
     * Geographic location latitude of this cell.
     */
	@io.swagger.annotations.ApiModelProperty("Geographic location latitude of this cell.")
	@JsonPropertyDescription("Geographic location latitude of this cell.")
	@Expose
    @SerializedName("latitude")
    @JsonProperty("latitude")
    private Double latitude;

    /**
     * Geographic location longitude of this cell.
     */
	@io.swagger.annotations.ApiModelProperty("Geographic location longitude of this cell.")
	@JsonPropertyDescription("Geographic location longitude of this cell.")
    @Expose
    @SerializedName("longitude")
    @JsonProperty("longitude")
    private Double longitude;

}
