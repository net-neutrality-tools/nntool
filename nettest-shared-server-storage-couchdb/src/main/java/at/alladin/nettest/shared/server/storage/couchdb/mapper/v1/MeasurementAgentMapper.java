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
