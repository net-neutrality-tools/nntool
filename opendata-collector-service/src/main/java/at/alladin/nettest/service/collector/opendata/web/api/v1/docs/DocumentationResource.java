package at.alladin.nettest.service.collector.opendata.web.api.v1.docs;

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
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.PointInTimeValueDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ReasonDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SpeedMeasurementRawDataItemDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.TrafficDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.WebSocketInfoDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementType;
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
		return "opendata_collector_v1";
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
		return Arrays.asList();
	}
}
