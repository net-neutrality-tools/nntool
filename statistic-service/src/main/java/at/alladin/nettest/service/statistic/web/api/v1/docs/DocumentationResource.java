package at.alladin.nettest.service.statistic.web.api.v1.docs;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiBase;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiError;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefSubMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroup;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroupItem;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.CellInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.DeviceInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.FullRttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeneralMeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkPointInTimeInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.OperatingSystemInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.PointInTimeValueDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ReasonDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SpeedMeasurementRawDataItemDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.TrafficDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.WebSocketInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.version.VersionResponse;
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
		return "statistic-service_v1";
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.web.api.AbstractDocumentationResource#getDtoClasses()
	 */
	@Override
	public List<Class<?>> getDtoClasses() {
		return Arrays.asList(
			ApiBase.class, 
			ApiRequest.class, 
			ApiRequestInfo.class, 
			BasicRequest.class,
			ApiResponse.class, 
			ApiError.class, 
			BasicResponse.class,
			
			BriefMeasurementResponse.class,
			BriefQoSMeasurement.class,
			BriefSpeedMeasurement.class,
			BriefSubMeasurement.class,
			
			EvaluatedQoSResult.class,
			FullMeasurementResponse.class,
			FullQoSMeasurement.class,
			FullSpeedMeasurement.class,
			FullSubMeasurement.class,
			
			DetailMeasurementGroup.class,
			DetailMeasurementGroupItem.class,
			DetailMeasurementResponse.class,
			
			GeneralMeasurementTypeDto.class,
			
			CellInfoDto.class,
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
			QoSMeasurementTypeDto.class,
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
		return Arrays.asList();
	}
}
