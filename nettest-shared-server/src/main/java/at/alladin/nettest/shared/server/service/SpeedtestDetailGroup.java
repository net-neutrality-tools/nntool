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

package at.alladin.nettest.shared.server.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SpeedtestDetailGroup {

	/**
	 * The key associated w/the detail group
	 * Is the same as the translation key for the given detail group
	 */
	@JsonProperty
	@Expose
	private String key;
	
	/**
	 * The icon of the detail group
	 */
	@JsonProperty
	@Expose
	private String icon;
	
	/**
	 * The single entries of the group (e.g. open_test_uuid, device_info.model, ...)
	 */
	@JsonProperty
	@Expose
	private List<SpeedtestDetailGroupEntry> values = new ArrayList<>();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public List<SpeedtestDetailGroupEntry> getValues() {
		return values;
	}

	public void setValues(List<SpeedtestDetailGroupEntry> values) {
		this.values = values;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpeedtestDetailGroup [key=" + key + ", icon=" + icon + ", values=" + values + "]";
	}
	
	public static class SpeedtestDetailGroupEntry {

		/**
		 * The key of an entry in the values of a speedtestDetailGroup
		 */
		@JsonProperty
		@Expose
		private String key;
		
		/**
		 * Key to find the translated title
		 */
		@JsonProperty("translation_key")
		@SerializedName("translation_key")
		@Expose
		private String translationKey;
		
		/**
		 * The unit to be appended to the value, optional
		 */
		@JsonProperty("unit")
		@SerializedName("unit")
		@Expose
		private String unit;
		
		/**
		 * Defines the format to be applied to the value of the given key, optional
		 */
		@JsonProperty("format")
		@SerializedName("format")
		@Expose
		private FormatEnum format;

		@JsonProperty
		@SerializedName("value")
		@Expose
		private String value;
		
		/**
		 * If the entry has a share text, it will be added to the share text sent to the clients
		 * An additional share priority may be provided to sort the share text as desired
		 */
		@JsonProperty
		@SerializedName("share_text")
		@Expose
		private String shareText;
		
		/*
		 * The order in which the corresponding share texts are assembled
		 * Lower means earlier and ties are broken arbitrarily
		 * If no value is provided, Integer.MAX will be assigned
		 */
		@JsonProperty
		@SerializedName("share_priority")
		@Expose
		private Integer sharePriority;
		
		public String getTranslationKey() {
			return translationKey;
		}

		public void setTranslationKey(String translationKey) {
			this.translationKey = translationKey;
		}

		public String getKey() {
			return key;
		}
		
		public void setKey(String key) {
			this.key = key;
		}
		
		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public FormatEnum getFormat() {
			return format;
		}

		public void setFormat(FormatEnum format) {
			this.format = format;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
		public String getShareText() {
			return shareText;
		}

		public void setShareText(String shareText) {
			this.shareText = shareText;
		}

		public Integer getSharePriority() {
			return sharePriority;
		}

		public void setSharePriority(Integer sharePriority) {
			this.sharePriority = sharePriority;
		}

		@Override
		public String toString() {
			return "SpeedtestDetailGroupEntry [key=" + key + ", translationKey=" + translationKey + ", unit=" + unit
					+ ", format=" + format + ", value=" + value + "]";
		}
		/**
		 * Enumeration to define possible formattings for a SpeedtestDetailGroupEntry
		 * TODO: rename formatEnums in a smart way
		 */
		public enum FormatEnum{
			DIV_1000(1000d),	// => / 1000d
			DIV_1E6(1000000d),	// => / 1000000d
			DIV_1E9(1000000000d),	// => / 1000000000d
			DIV_1E12(1000000000000d), 	// => / 1000000000000d
			TRANSLATE_VALUE(1d),
			TRANSLATE_BOOLEAN_VALUE(1d),
			TRUNCATE(1d),	//truncates the number of digits after a dot
			TIMESTAMP(1d)	//formats the value into the correct timestring format
			;
			
			
			private final double divider;
			
			FormatEnum(double divider) {
				this.divider = divider;
			}
			
			public double getDivider() {
				return divider;
			}
		}
	}
}
