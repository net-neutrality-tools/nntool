package at.alladin.nettest.service.collector.web.api.v1.docs;

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
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.settings.SettingsRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ip.IpResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementType;
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
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementInitiationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementInitiationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementTypeRequestOptions;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementRequestOptions;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParams;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementRequestOptions;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParams;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultNetworkPointInTime;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.QoSMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SubMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.CellLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ClientInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ClientTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.DeviceInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.FullRttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
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
import at.alladin.nettest.shared.server.domain.model.CellLocation;
import at.alladin.nettest.shared.server.domain.model.CellLocationInfo;
import at.alladin.nettest.shared.server.domain.model.Client;
import at.alladin.nettest.shared.server.domain.model.ClientInfo;
import at.alladin.nettest.shared.server.domain.model.ClientType;
import at.alladin.nettest.shared.server.domain.model.ConnectionInfo;
import at.alladin.nettest.shared.server.domain.model.Device;
import at.alladin.nettest.shared.server.domain.model.DeviceInfo;
import at.alladin.nettest.shared.server.domain.model.EmbeddedNetworkType;
import at.alladin.nettest.shared.server.domain.model.EmbeddedProvider;
import at.alladin.nettest.shared.server.domain.model.GeoLocation;
import at.alladin.nettest.shared.server.domain.model.GeoLocationInfo;
import at.alladin.nettest.shared.server.domain.model.MccMnc;
import at.alladin.nettest.shared.server.domain.model.Measurement;
import at.alladin.nettest.shared.server.domain.model.MeasurementServer;
import at.alladin.nettest.shared.server.domain.model.MeasurementServerInfo;
import at.alladin.nettest.shared.server.domain.model.MeasurementServerType;
import at.alladin.nettest.shared.server.domain.model.MeasurementStatusInfo;
import at.alladin.nettest.shared.server.domain.model.MeasurementTime;
import at.alladin.nettest.shared.server.domain.model.NetworkInfo;
import at.alladin.nettest.shared.server.domain.model.NetworkMobileInfo;
import at.alladin.nettest.shared.server.domain.model.NetworkType;
import at.alladin.nettest.shared.server.domain.model.NetworkTypeCategory;
import at.alladin.nettest.shared.server.domain.model.NetworkWifiInfo;
import at.alladin.nettest.shared.server.domain.model.NumStreamsInfo;
import at.alladin.nettest.shared.server.domain.model.OperatingSystemInfo;
import at.alladin.nettest.shared.server.domain.model.Provider;
import at.alladin.nettest.shared.server.domain.model.ProviderAsnMapping;
import at.alladin.nettest.shared.server.domain.model.ProviderInfo;
import at.alladin.nettest.shared.server.domain.model.ProviderMccMncMapping;
import at.alladin.nettest.shared.server.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.domain.model.QoSMeasurementType;
import at.alladin.nettest.shared.server.domain.model.QoSResult;
import at.alladin.nettest.shared.server.domain.model.RoamingType;
import at.alladin.nettest.shared.server.domain.model.Rtt;
import at.alladin.nettest.shared.server.domain.model.RttInfo;
import at.alladin.nettest.shared.server.domain.model.Settings;
import at.alladin.nettest.shared.server.domain.model.Signal;
import at.alladin.nettest.shared.server.domain.model.SignalInfo;
import at.alladin.nettest.shared.server.domain.model.SpeedMeasurement;
import at.alladin.nettest.shared.server.domain.model.SpeedMeasurementRawData;
import at.alladin.nettest.shared.server.domain.model.SpeedMeasurementRawDataItem;
import at.alladin.nettest.shared.server.domain.model.StatisticalTimeSeriesData;
import at.alladin.nettest.shared.server.domain.model.SubMeasurement;
import at.alladin.nettest.shared.server.domain.model.SubMeasurementTime;
import at.alladin.nettest.shared.server.domain.model.Traffic;
import at.alladin.nettest.shared.server.domain.model.Translation;
import at.alladin.nettest.shared.server.domain.model.WebSocketInfo;

/**
 * This controller provides the documentation resources (redirects to Swagger, JSON schemes of model and DTO classes).
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Controller
@RequestMapping("/api/v1/documentation")
public class DocumentationResource extends AbstractLmapDocumentationResource {

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.web.api.AbstractDocumentationResource#getOpenApiGroupName()
	 */
	@Override
	public String getOpenApiGroupName() {
		return "collector/controller_v1";
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

			MeasurementType.class,
			
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
			
			MeasurementInitiationRequest.class,
			MeasurementInitiationResponse.class,
			MeasurementTypeParameters.class,
			MeasurementTypeRequestOptions.class,
			QoSMeasurementRequestOptions.class,
			QoSMeasurementTypeParams.class,
			SpeedMeasurementRequestOptions.class,
			SpeedMeasurementTypeParams.class,
			
			MeasurementResultNetworkPointInTime.class,
			MeasurementResultRequest.class,
			MeasurementResultResponse.class,
			QoSMeasurementResult.class,
			SpeedMeasurementResult.class,
			SubMeasurementResult.class,
			
			CellLocationDto.class,
			ClientInfoDto.class,
			ClientTypeDto.class,
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
			Client.class,
			ClientInfo.class,
			ClientType.class,
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
			Traffic.class,
			Translation.class,
			WebSocketInfo.class
		);
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.service.collector.web.api.v1.docs.LmapAbstractDocumentationResource#getLmapControlClasses()
	 */
	@Override
	public List<Class<?>> getLmapControlClasses() {
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

			MeasurementType.class,
			
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
			
			MeasurementInitiationRequest.class,
			MeasurementInitiationResponse.class,
			MeasurementTypeParameters.class,
			MeasurementTypeRequestOptions.class,
			QoSMeasurementRequestOptions.class,
			QoSMeasurementTypeParams.class,
			SpeedMeasurementRequestOptions.class,
			SpeedMeasurementTypeParams.class,
			
			CellLocationDto.class,
			ClientInfoDto.class,
			ClientTypeDto.class,
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
	 * @see at.alladin.nettest.service.collector.web.api.v1.docs.LmapAbstractDocumentationResource#getLmapReportClasses()
	 */
	@Override
	public List<Class<?>> getLmapReportClasses() {
		return Arrays.asList(
			ApiBase.class, 
			ApiError.class, 
			ApiPagination.class,
			ApiRequest.class, 
			ApiRequestInfo.class, 
			ApiResponse.class, 
			BasicRequest.class,
			BasicResponse.class,

			MeasurementType.class,
			
			MeasurementResultRequest.class,
			MeasurementResultResponse.class,
			QoSMeasurementResult.class,
			SpeedMeasurementResult.class,
			SubMeasurementResult.class,
			
			CellLocationDto.class,
			ClientTypeDto.class,
			GeoLocationDto.class,
			PointInTimeValueDto.class,
			QoSMeasurementType.class,
			ReasonDto.class,
			RttDto.class,
			RttInfoDto.class,
			SignalDto.class,
			SpeedMeasurementRawDataItemDto.class,
			StatusDto.class,
			TrafficDto.class,
			WebSocketInfoDto.class
		);
	}
}
