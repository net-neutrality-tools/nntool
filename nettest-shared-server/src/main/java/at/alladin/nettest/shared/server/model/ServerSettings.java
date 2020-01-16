/*******************************************************************************
 * Copyright 2018-2019 alladin-IT GmbH
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

package at.alladin.nettest.shared.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.server.service.SpeedtestDetailGroup;

public class ServerSettings {

	/**
	 * The list defining the groups returned in the detail measurement response.
	 */
	@JsonPropertyDescription("The list defining the groups returned in the detail measurement response.")
	@JsonProperty("speedtest_detail_groups")
	@SerializedName("speedtest_detail_groups")
	@Expose
	private List<SpeedtestDetailGroup> speedtestDetailGroups = new ArrayList<>();
	
	@Expose
	@SerializedName("speed_thresholds")
	@JsonProperty("speed_thresholds")
	private SpeedThresholds speedThresholds = new SpeedThresholds();
	
	public List<SpeedtestDetailGroup> getSpeedtestDetailGroups() {
		return speedtestDetailGroups;
	}
	
	public void setSpeedtestDetailGroups(List<SpeedtestDetailGroup> speedtestDetailGroups) {
		this.speedtestDetailGroups = speedtestDetailGroups;
	}
	
	public SpeedThresholds getSpeedThresholds() {
		return speedThresholds;
	}

	public void setSpeedThresholds(SpeedThresholds speedThresholds) {
		this.speedThresholds = speedThresholds;
	}

	public static class SpeedThresholds {
		
		@Expose
		private ColorThresholds upload;
		
		@Expose
		private ColorThresholds download;
		
		@Expose
		private ColorThresholds ping;
		
		@Expose
		private ColorThresholds signal;

		public ColorThresholds getUpload() {
			return upload;
		}

		public void setUpload(ColorThresholds upload) {
			this.upload = upload;
		}

		public ColorThresholds getDownload() {
			return download;
		}

		public void setDownload(ColorThresholds download) {
			this.download = download;
		}

		public ColorThresholds getPing() {
			return ping;
		}

		public void setPing(ColorThresholds ping) {
			this.ping = ping;
		}

		public ColorThresholds getSignal() {
			return signal;
		}

		public void setSignal(ColorThresholds signal) {
			this.signal = signal;
		}

		@Override
		public String toString() {
			return "SpeedThresholds [upload=" + upload + ", download=" + download + ", ping=" + ping + ", signal="
					+ signal + "]";
		}
	}
	
	public static class ColorThresholds {
		/**
		 * Default color to be used when the achieved result is bigger than the highest entry in the colorMap
		 */
		@Expose
		@SerializedName("default_color")
		@JsonProperty("default_color")
		private String defaultColor;
		
		/**
		 * The smallest key higher than the achieved result equals the color to be used (use: colorMap.ceilingKey(YOUR_KEY) )
		 */
		@Expose
		@SerializedName("color_map")
		@JsonProperty("color_map")
		private TreeMap<Long, String> colorMap;

		public String getDefaultColor() {
			return defaultColor;
		}

		public void setDefaultColor(String defaultColor) {
			this.defaultColor = defaultColor;
		}

		public TreeMap<Long, String> getColorMap() {
			return colorMap;
		}

		public void setColorMap(TreeMap<Long, String> colorMap) {
			this.colorMap = colorMap;
		}

		@Override
		public String toString() {
			return "ColorThresholds [defaultColor=" + defaultColor + ", colorMap=" + colorMap + "]";
		}
	}
}
