package at.alladin.nettest.service.controller.web.api.v1.docs;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiBase;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiError;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ip.IpResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapFunctionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapOptionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapActionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapAgentDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapEventDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapEventTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapImmediateEventDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapScheduleDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapStateDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapStopDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapStopDurationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapStopEndDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapSuppressionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefSubMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroup;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroupItem;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementTypeRequestOptions;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementRequestOptions;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementRequestOptions;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.CellLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.DeviceInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.FullRttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeneralMeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkPointInTimeInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.OperatingSystemInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.PointInTimeValueDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ReasonDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SpeedMeasurementRawDataItemDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.TrafficDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.WebSocketInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.version.VersionResponse;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.CellLocation;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.CellLocationInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ConnectionInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Device;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.DeviceInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.EmbeddedNetworkType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.EmbeddedProvider;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.GeoLocation;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.GeoLocationInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MccMnc;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServer;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServerInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServerType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementStatusInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkMobileInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkTypeCategory;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkWifiInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NumStreamsInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.OperatingSystemInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Provider;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ProviderAsnMapping;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ProviderInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ProviderMccMncMapping;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSResult;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.RoamingType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Rtt;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.RttInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Signal;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SignalInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurementRawData;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurementRawDataItem;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.StatisticalTimeSeriesData;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SubMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SubMeasurementLmapResult;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SubMeasurementTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Traffic;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Translation;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.WebSocketInfo;
import at.alladin.nettest.shared.server.web.api.v1.AbstractDocumentationResource;

/**
 * This controller provides the documentation resources (redirects to Swagger, JSON schemes of model and DTO classes).
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Controller
@RequestMapping("/api/v1/documentation")
public class DocumentationResource extends AbstractDocumentationResource {

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.web.api.AbstractDocumentationResource#getOpenApiGroupName()
	 */
	@Override
	public String getOpenApiGroupName() {
		return "controller_v1";
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.web.api.AbstractDocumentationResource#getDtoClasses()
	 */
	@Override
	public List<Class<?>> getDtoClasses() {
		return Arrays.asList(
			ApiBase.class, 
			ApiError.class, 
			ApiPagination.class,
			ApiRequest.class, 
			ApiRequestInfo.class, 
			ApiResponse.class, 
			BasicRequest.class,
			BasicResponse.class,

			RegistrationRequest.class, 
			RegistrationResponse.class,

			SettingsRequest.class, 
			SettingsResponse.class,
			
			IpResponse.class,

			MeasurementTypeDto.class,
			GeneralMeasurementTypeDto.class,
			
			BriefMeasurementResponse.class,
			BriefQoSMeasurement.class,
			BriefSpeedMeasurement.class,
			BriefSubMeasurement.class,
			
			DetailMeasurementGroup.class,
			DetailMeasurementGroupItem.class,
			DetailMeasurementResponse.class,
			
			DisassociateResponse.class,
			
			EvaluatedQoSResult.class,
			FullMeasurementResponse.class,
			FullQoSMeasurement.class,
			FullSpeedMeasurement.class,
			FullSubMeasurement.class,
			
			LmapControlDto.class,
			LmapOptionDto.class,
			LmapActionDto.class,
			LmapAgentDto.class,
			LmapCapabilityDto.class,
			LmapCapabilityTaskDto.class,
			LmapEventDto.class,
			LmapEventTypeDto.class,
			LmapFunctionDto.class,
			LmapImmediateEventDto.class,
			LmapScheduleDto.class,
			LmapStateDto.class,
			LmapStopDto.class,
			LmapStopDurationDto.class,
			LmapStopEndDto.class,
			LmapSuppressionDto.class,
			LmapTaskDto.class,
			
			MeasurementTypeParameters.class,
			MeasurementTypeRequestOptions.class,
			QoSMeasurementRequestOptions.class,
			QoSMeasurementTypeParameters.class,
			SpeedMeasurementRequestOptions.class,
			SpeedMeasurementTypeParameters.class,
			
			CellLocationDto.class,
			MeasurementAgentInfoDto.class,
			MeasurementAgentTypeDto.class,
			ConnectionInfoDto.class,
			DeviceInfoDto.class,
			FullRttInfoDto.class,
			GeoLocationDto.class,
			NetworkInfoDto.class,
			NetworkPointInTimeInfoDto.class,
			OperatingSystemInfoDto.class,
			PointInTimeValueDto.class,
			QoSMeasurementType.class,
			ReasonDto.class,
			RttDto.class,
			RttInfoDto.class,
			SignalDto.class,
			SpeedMeasurementRawDataItemDto.class,
			StatusDto.class,
			TrafficDto.class,
			WebSocketInfoDto.class,
			
			VersionResponse.class
		);
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.web.api.AbstractDocumentationResource#getModelClasses()
	 */
	@Override
	public List<Class<?>> getModelClasses() {
		return Arrays.asList(
			CellLocation.class,
			CellLocationInfo.class,
			MeasurementAgent.class,
			MeasurementAgentInfo.class,
			MeasurementAgentType.class,
			ConnectionInfo.class,
			Device.class,
			DeviceInfo.class,
			EmbeddedNetworkType.class,
			EmbeddedProvider.class,
			GeoLocation.class,
			GeoLocationInfo.class,
			MccMnc.class,
			Measurement.class,
			MeasurementServer.class,
			MeasurementServerInfo.class,
			MeasurementServerType.class,
			MeasurementStatusInfo.class,
			MeasurementTime.class,
			NetworkInfo.class,
			NetworkMobileInfo.class,
			NetworkType.class,
			NetworkTypeCategory.class,
			NetworkWifiInfo.class,
			NumStreamsInfo.class,
			OperatingSystemInfo.class,
			Provider.class,
			ProviderAsnMapping.class,
			ProviderInfo.class,
			ProviderMccMncMapping.class,
			QoSMeasurement.class,
			QoSMeasurementObjective.class,
			QoSResult.class,
			RoamingType.class,
			Rtt.class,
			RttInfo.class,
			Settings.class,
			Signal.class,
			SignalInfo.class,
			SpeedMeasurement.class,
			SpeedMeasurementRawData.class,
			SpeedMeasurementRawDataItem.class,
			StatisticalTimeSeriesData.class,
			SubMeasurement.class,
			SubMeasurementTime.class,
			SubMeasurementLmapResult.class,
			Traffic.class,
			Translation.class,
			WebSocketInfo.class
		);
	}
}
