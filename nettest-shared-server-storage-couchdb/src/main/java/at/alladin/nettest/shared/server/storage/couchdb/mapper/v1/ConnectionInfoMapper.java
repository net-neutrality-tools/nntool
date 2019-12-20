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

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ConnectionInfo;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Mapper(componentModel = "spring")
public interface ConnectionInfoMapper {
	
	@Mappings({
		@Mapping(target="requestedNumStreamsDownload", source="numStreamsInfo.requestedNumStreamsDownload"),
		@Mapping(target="requestedNumStreamsUpload", source="numStreamsInfo.requestedNumStreamsUpload"),
		@Mapping(target="actualNumStreamsDownload", source="numStreamsInfo.actualNumStreamsDownload"),
		@Mapping(target="actualNumStreamsUpload", source="numStreamsInfo.actualNumStreamsUpload"),
		@Mapping(target="agentInterfaceTotalTraffic", source="clientInterfaceTotalTraffic"),
		@Mapping(target="agentInterfaceDownloadMeasurementTraffic", source="clientInterfaceDownloadMeasurementTraffic"),
		@Mapping(target="agentInterfaceUploadMeasurementTraffic", source="clientInterfaceUploadMeasurementTraffic"),
	})
	ConnectionInfoDto map(ConnectionInfo arg0);

}
