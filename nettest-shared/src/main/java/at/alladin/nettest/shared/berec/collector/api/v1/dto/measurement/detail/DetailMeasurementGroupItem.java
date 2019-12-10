package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class specifies a single detail item with key, translated title, value and the unit of the value.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This class specifies a single detail item with key, translated title, value and the unit of the value.")
@JsonClassDescription("This class specifies a single detail item with key, translated title, value and the unit of the value.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailMeasurementGroupItem {
	
	/**
	 * The key is the path to the chosen field inside the data model, e.g. "device_info.model".
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The key is the path to the chosen field inside the data model, e.g. \"device_info.model\".")
	@JsonPropertyDescription("The key is the path to the chosen field inside the data model, e.g. \"device_info.model\".")
	@Expose
	@SerializedName("key")
	@JsonProperty(required = true, value = "key")
	private String key;
	
	/**
	 * The translated title of this item.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The translated title of this item.")
	@JsonPropertyDescription("The translated title of this item.")
	@Expose
	@SerializedName("title")
	@JsonProperty(required = true, value = "title")
	private String title;
	
	/**
	 * The actual value of this item in the given unit.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual value of this item in the given unit.")
	@JsonPropertyDescription("The actual value of this item in the given unit.")
	@Expose
	@SerializedName("value")
	@JsonProperty(required = true, value = "value")
	private String value;
	
	/**
	 * The unit of the value.
	 */
	@io.swagger.annotations.ApiModelProperty("The unit of the value.")
	@JsonPropertyDescription("The unit of the value.")
	@Expose
	@SerializedName("unit")
	@JsonProperty("unit")
	private String unit;

	/**
	 * The classification number of the item.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The classification number of the item.")
	@JsonPropertyDescription("The classification number of the item.")
	@Expose
	private Integer classification;

	/**
	 * The classification color to be associated with the item.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The classification color to be associated with the item.")
	@JsonPropertyDescription("The classification color to be associated with the item.")
	@Expose
	@SerializedName("classification_color")
	@JsonProperty("classification_color")
	private String classificationColor;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getClassification() {
		return classification;
	}

	public void setClassification(Integer classification) {
		this.classification = classification;
	}

	public String getClassificationColor() {
		return classificationColor;
	}

	public void setClassificationColor(String classificationColor) {
		this.classificationColor = classificationColor;
	}

	@Override
	public String toString() {
		return "DetailMeasurementGroupItem [key=" + key + ", title=" + title + ", value=" + value + ", unit=" + unit
				+ ", classification=" + classification + ", classificationColor=" + classificationColor + "]";
	}
	
}
