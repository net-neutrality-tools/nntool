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

package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.RttInfo;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Mapper(componentModel = "spring")
public interface RttInfoDtoMapper {
	
	@Mappings({
		@Mapping(target="minNs", source="minimumNs"),
		@Mapping(target="maxNs", source="maximumNs"),
		@Mapping(target="minLog", expression="java(arg0 == null ? "
				+ "null : arg0.getMinimumNs() == null ? "
				+ "null : arg0.getMinimumNs() == 0 ? "
				+ "null : Math.log10(arg0.getMinimumNs()))"),
		@Mapping(target="maxLog", expression="java(arg0 == null ? "
				+ "null : arg0.getMaximumNs() == null ? "
				+ "null : arg0.getMaximumNs() == 0 ? "
				+ "null : Math.log10(arg0.getMaximumNs()))"),
		@Mapping(target="averageLog", expression="java(arg0 == null ? "
				+ "null : arg0.getAverageNs() == null ? "
				+ "null : arg0.getAverageNs() == 0 ? "
				+ "null : Math.log10(arg0.getAverageNs()))"),
		@Mapping(target="medianLog", expression="java(arg0 == null ? "
				+ "null : arg0.getMedianNs() == null ? "
				+ "null : arg0.getMedianNs() == 0 ? "
				+ "null : Math.log10(arg0.getMedianNs()))"),
		@Mapping(target="variance", expression="java(arg0 == null ? "
				+ "null : arg0.getStandardDeviationNs() == null ? "
				+ "null : arg0.getStandardDeviationNs() == 0 ? "
				+ "null : arg0.getStandardDeviationNs() * arg0.getStandardDeviationNs())"),
		@Mapping(target="address", source="address")
	})
	RttInfo map(RttInfoDto arg0);

}
