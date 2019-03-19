package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.SettingsRepository;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapReportModelMapperImpl;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	LmapReportModelMapperImpl.class,
	CouchDbStorageService.class
})
@AutoConfigureJsonTesters
public class CouchDbStorageServiceTest {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("classpath:lmap_report_model/model1.json")
	private Resource model1Resource;
	
	@MockBean
	private MeasurementRepository measurementRepository;
	
	@MockBean
	private SettingsRepository settingsRepository;
	
	@Autowired
	private StorageService storageService;
	
	@Test
	public void testCouchDbStorageServiceSave() throws JsonParseException, JsonMappingException, IOException {
		final LmapReportDto lmapReportDto = objectMapper.readValue(model1Resource.getInputStream(), LmapReportDto.class);
		
		when(measurementRepository.save(any(Measurement.class))).then(returnsFirstArg());
		
		final MeasurementResultResponse resultResponse = storageService.save(lmapReportDto);
		
		verify(measurementRepository, times(1)).save(any(Measurement.class));
		
		assertThat(resultResponse.getUuid(), not(isEmptyOrNullString()));
		assertThat(resultResponse.getOpenDataUuid(), not(isEmptyOrNullString()));
		
		assertNotNull(UUID.fromString(resultResponse.getUuid()));
		assertNotNull(UUID.fromString(resultResponse.getOpenDataUuid()));
	}
}
