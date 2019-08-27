package at.alladin.nettest.service.collector.web.api.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.service.collector.config.CollectorServiceProperties;
import at.alladin.nettest.service.collector.service.MeasurementResultService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureJsonTesters
public class MeasurementResultResourceIntegrationTest {

    private MockMvc mockMvc;
	
    @Autowired
    private ObjectMapper objectMapper;
    
	@MockBean
	private StorageService storageService;
	
	@MockBean
	private CollectorServiceProperties collectorServiceProperties;
	
	@Before
	public void setup() {
		final MeasurementResultService measurementResultService = new MeasurementResultService();
		ReflectionTestUtils.setField(measurementResultService, "storageService", storageService);
		ReflectionTestUtils.setField(measurementResultService, "collectorServiceProperties", collectorServiceProperties);
		
		final MeasurementResultResource controller = new MeasurementResultResource();
		ReflectionTestUtils.setField(controller, "measurementResultService", measurementResultService);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	
	@Test
	public void testPostLmapReportModelReturnsValidMeasurementResultResponse() throws Exception {
		final MeasurementResultResponse resultResponse = new MeasurementResultResponse();
		resultResponse.setUuid(UUID.randomUUID().toString());
		resultResponse.setOpenDataUuid(UUID.randomUUID().toString());
		
		when(storageService.save(any(LmapReportDto.class), any(String.class))).thenReturn(resultResponse);
		when(collectorServiceProperties.getSystemUuid()).thenReturn(UUID.randomUUID().toString());
		
		mockMvc
			.perform(
				post("/api/v1/measurements")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(objectMapper.writeValueAsString(new LmapReportDto()))
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("data.uuid", is(resultResponse.getUuid())))
			.andExpect(jsonPath("data.open_data_uuid", is(resultResponse.getOpenDataUuid())));
		
		verify(storageService, times(1)).save(any(LmapReportDto.class), any(String.class));
	}
	
	// TODO: test if storageService throws exception
}
