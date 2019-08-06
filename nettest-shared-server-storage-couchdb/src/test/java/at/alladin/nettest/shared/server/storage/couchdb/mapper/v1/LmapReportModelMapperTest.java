package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.TimeBasedResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.DeviceInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.OperatingSystemInfo;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	LmapReportModelMapperImpl.class
})
@AutoConfigureJsonTesters
public class LmapReportModelMapperTest {

	@Autowired
	private LmapReportModelMapper lmapReportModelMapper;
	
	@Autowired
	private DateTimeMapper dateTimeMapper;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("classpath:lmap_report_model/model1.json")
	private Resource model1Resource;
	
	@Test
	public void testMapLmapReportModel() throws JsonParseException, JsonMappingException, IOException {
		final LmapReportDto lmapReportDto = objectMapper.readValue(model1Resource.getInputStream(), LmapReportDto.class);
		final ApiRequestInfo addReqInfo = lmapReportDto.getAdditionalRequestInfo();
		final TimeBasedResultDto timeBasedResultDto = lmapReportDto.getTimeBasedResult();
		
		final Measurement measurement = lmapReportModelMapper.map(lmapReportDto);
		final MeasurementAgentInfo agentInfo = measurement.getAgentInfo();
		final DeviceInfo deviceInfo = measurement.getDeviceInfo();
		final OperatingSystemInfo osInfo = deviceInfo.getOsInfo();
		final MeasurementTime measurementTime = measurement.getMeasurementTime();
		
		assertNull(measurement.getId());
		assertNull(measurement.getRev());
		
		assertNull(measurement.getUuid());
		assertNull(measurement.getOpenDataUuid());
		
		assertEquals(dateTimeMapper.map(lmapReportDto.getDate()), measurement.getSubmitTime());
		assertEquals(lmapReportDto.getAgentId(), agentInfo.getUuid());
		
		assertEquals(addReqInfo.getLanguage(),  agentInfo.getLanguage());
		assertEquals(addReqInfo.getTimezone(),  agentInfo.getTimezone());
		assertEquals(addReqInfo.getAgentType(), agentInfo.getType());

		assertEquals(addReqInfo.getOsName(), 	osInfo.getName());
		assertEquals(addReqInfo.getOsVersion(), osInfo.getVersion());
		assertEquals(addReqInfo.getApiLevel(), 	osInfo.getApiLevel());
		
		assertEquals(addReqInfo.getCodeName(), deviceInfo.getCodeName());
		assertEquals(addReqInfo.getModel(),    deviceInfo.getModel());
		assertNull(deviceInfo.getFullName());

		assertEquals(addReqInfo.getAppVersionName(), agentInfo.getAppVersionName());
		assertEquals(addReqInfo.getAppVersionCode(), agentInfo.getAppVersionCode());
		assertEquals(addReqInfo.getAppGitRevision(), agentInfo.getAppGitRevision());
		
		assertEquals(dateTimeMapper.map(timeBasedResultDto.getStartTime()), measurementTime.getStartTime());
		assertEquals(dateTimeMapper.map(timeBasedResultDto.getEndTime()), measurementTime.getEndTime());
		assertEquals(timeBasedResultDto.getDurationNs(), measurementTime.getDurationNs());
		
		// TODO: more
	}
	
	@Test
	public void testGeolocationDistanceParsingWithTwoValidValues() {
		final LmapReportDto reportDto = new LmapReportDto();
		final TimeBasedResultDto timeBasedResult = new TimeBasedResultDto();
		reportDto.setTimeBasedResult(timeBasedResult);
		final List<GeoLocationDto> geoLocations = new ArrayList<>();
		timeBasedResult.setGeoLocations(geoLocations);
		
		GeoLocationDto point = new GeoLocationDto();
		point.setLatitude(46.837817);
		point.setLongitude(12.768663);
		geoLocations.add(point);
		
		point = new GeoLocationDto();
		point.setLatitude(46.837848);
		point.setLongitude(12.767668);
		geoLocations.add(point);
		
		final Measurement measurement = lmapReportModelMapper.map(reportDto);
		//the correct distances are according to https://www.movable-type.co.uk/scripts/latlong.html and verified via google maps
		assertEquals("Unexpected distance calculated", 75, measurement.getGeoLocationInfo().getDistanceMovedMetres(), 1);
	}

	@Test
	public void testGeolocationDistanceParsingWithMultipleValues() {
		final LmapReportDto reportDto = new LmapReportDto();
		final TimeBasedResultDto timeBasedResult = new TimeBasedResultDto();
		reportDto.setTimeBasedResult(timeBasedResult);
		final List<GeoLocationDto> geoLocations = new ArrayList<>();
		timeBasedResult.setGeoLocations(geoLocations);

		GeoLocationDto point = new GeoLocationDto();
		geoLocations.add(point);

		point = new GeoLocationDto();
		point.setLatitude(46.837817);
		point.setLongitude(12.768663);
		geoLocations.add(point);

		geoLocations.add(new GeoLocationDto());

		point = new GeoLocationDto();
		point.setLatitude(46.837848);
		point.setLongitude(12.767668);
		geoLocations.add(point);
		//~75 m to this point

		//moving back a little
		point = new GeoLocationDto();
		point.setLatitude(46.837939);
		point.setLongitude(12.768037);
		geoLocations.add(point);
		//~ 29m to this point (from the previous point)

		final Measurement measurement = lmapReportModelMapper.map(reportDto);
		//the correct distances are according to https://www.movable-type.co.uk/scripts/latlong.html and verified via google maps
		assertEquals("Unexpected distance calculated", 104, measurement.getGeoLocationInfo().getDistanceMovedMetres(), 1);
	}

	@Test
	public void testGeoLocationDistanceParsingWithInsufficientValues_producesNoDistanceMoved() {
		final LmapReportDto reportDto = new LmapReportDto();
		final TimeBasedResultDto timeBasedResult = new TimeBasedResultDto();
		reportDto.setTimeBasedResult(timeBasedResult);
		final List<GeoLocationDto> geoLocations = new ArrayList<>();
		timeBasedResult.setGeoLocations(geoLocations);

		GeoLocationDto point = new GeoLocationDto();
		point.setLongitude(12.768037);
		geoLocations.add(point);

		geoLocations.add(new GeoLocationDto());

		point = new GeoLocationDto();
		point.setLatitude(46.837939);
		point.setLongitude(12.768037);
		geoLocations.add(point);

		final Measurement measurement = lmapReportModelMapper.map(reportDto);
		assertNull("unexpected distance obtained", measurement.getGeoLocationInfo().getDistanceMovedMetres());
	}
}
