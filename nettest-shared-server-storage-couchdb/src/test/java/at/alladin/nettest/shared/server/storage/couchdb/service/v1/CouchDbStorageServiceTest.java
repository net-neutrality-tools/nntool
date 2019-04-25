package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementAgentRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.QoSMeasurementObjectiveRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.SettingsRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.TaskConfigurationQoSRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.TaskConfigurationSpeedRepository;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.BriefMeasurementResponseMapperImpl;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.FullMeasurementResponseMapperImpl;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapReportModelMapperImpl;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapTaskMapperImpl;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.MeasurementAgentMapperImpl;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.SettingsResponseMapperImpl;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	MeasurementAgentMapperImpl.class, 
	FullMeasurementResponseMapperImpl.class,
	LmapReportModelMapperImpl.class, 
	LmapTaskMapperImpl.class, 
	SettingsResponseMapperImpl.class,
	LmapReportModelMapperImpl.class,
	CouchDbStorageService.class,
	QoSEvaluationService.class,
	BriefMeasurementResponseMapperImpl.class,
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
	
	@MockBean
	private MeasurementAgentRepository measurementAgentRepository;
	
	@MockBean
	private TaskConfigurationSpeedRepository taskConfigurationSpeedRepository;

	@MockBean
	private TaskConfigurationQoSRepository taskConfigurationQoSRepository;
	
	@MockBean
	private QoSMeasurementObjectiveRepository qosMeasurementObjectiveRepository;
	
	@MockBean
	private DetailMeasurementService detailMeasurementService;
	
	@Autowired
	private StorageService storageService;
	
	@Test(expected=StorageServiceException.class)
	public void testCouchDbStorageServiceSaveWithoutValidAgentIdThrowsException() throws JsonParseException, JsonMappingException, IOException {
		final LmapReportDto lmapReportDto = objectMapper.readValue(model1Resource.getInputStream(), LmapReportDto.class);
		
		when(measurementRepository.save(any(Measurement.class))).then(returnsFirstArg());
		
		final MeasurementResultResponse resultResponse = storageService.save(lmapReportDto);
		
	}
}
