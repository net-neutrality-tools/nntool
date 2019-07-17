package at.alladin.nettest.service.collector.web.api.v1.docs;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapConflictDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.QoSMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SubMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.TimeBasedResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.CellInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.PointInTimeValueDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ReasonDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SpeedMeasurementRawDataItemDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.TrafficDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.WebSocketInfoDto;
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
		return "collector_v1";
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.web.api.AbstractDocumentationResource#getDtoClasses()
	 */
	@Override
	public List<Class<?>> getDtoClasses() {
		return Arrays.asList(
			ApiRequestInfo.class, 

			LmapReportDto.class,
			LmapConflictDto.class,
			LmapResultDto.class,
			TimeBasedResultDto.class,
			
			MeasurementResultResponse.class,
			QoSMeasurementResult.class,
			SpeedMeasurementResult.class,
			SubMeasurementResult.class,
			
			CellInfoDto.class,
			MeasurementAgentTypeDto.class,
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
