package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ConnectionInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.RttInfo;

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
