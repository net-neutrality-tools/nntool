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

package at.alladin.nettest.service.map.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Provider {
	
	private String name;
	
	private String shortname;
	
	private List<ProviderMccMncMapping> mccMncMappings;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public List<ProviderMccMncMapping> getMccMncMappings() {
		return mccMncMappings;
	}

	public void setMccMncMappings(List<ProviderMccMncMapping> mccMncMappings) {
		this.mccMncMappings = mccMncMappings;
	}

	public static class ProviderMccMncMapping {
		private MccMnc simMccMnc;
		
		private MccMnc networkMccMnc;
		
		private LocalDateTime conditionValidFrom;
		
		private LocalDateTime conditionValidTo;

		public MccMnc getSimMccMnc() {
			return simMccMnc;
		}

		public void setSimMccMnc(MccMnc simMccMnc) {
			this.simMccMnc = simMccMnc;
		}

		public MccMnc getNetworkMccMnc() {
			return networkMccMnc;
		}

		public void setNetworkMccMnc(MccMnc networkMccMnc) {
			this.networkMccMnc = networkMccMnc;
		}

		public LocalDateTime getConditionValidFrom() {
			return conditionValidFrom;
		}

		public void setConditionValidFrom(LocalDateTime conditionValidFrom) {
			this.conditionValidFrom = conditionValidFrom;
		}

		public LocalDateTime getConditionValidTo() {
			return conditionValidTo;
		}

		public void setConditionValidTo(LocalDateTime conditionValidTo) {
			this.conditionValidTo = conditionValidTo;
		}

	}
	
	public static class MccMnc {
		
		private Integer mcc;
		
		private Integer mnc;

		public Integer getMcc() {
			return mcc;
		}

		public void setMcc(Integer mcc) {
			this.mcc = mcc;
		}

		public Integer getMnc() {
			return mnc;
		}

		public void setMnc(Integer mnc) {
			this.mnc = mnc;
		}
		
	}
}
