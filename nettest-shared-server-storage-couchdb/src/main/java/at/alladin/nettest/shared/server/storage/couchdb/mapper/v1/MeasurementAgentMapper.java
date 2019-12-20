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

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;

@Mapper(componentModel = "spring")
public interface MeasurementAgentMapper {

	@Mappings({
		@Mapping(source = "data.termsAndConditionsAccepted", target = "termsAndConditionsAccepted"),
		@Mapping(source = "requestInfo.agentType", target = "type"),
		@Mapping(source = "data.groupName", target = "groupName"),
		@Mapping(source = "data.termsAndConditionsAcceptedVersion", target = "termsAndConditionsAcceptedVersion"),
		@Mapping(source = "requestInfo.agentId", target = "uuid")
	})
	MeasurementAgent map(ApiRequest<RegistrationRequest> registrationRequest);
	
}
