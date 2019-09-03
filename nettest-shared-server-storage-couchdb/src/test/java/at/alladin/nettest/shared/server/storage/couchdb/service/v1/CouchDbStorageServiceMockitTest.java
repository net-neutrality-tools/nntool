package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.EmbeddedNetworkTypeRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementAgentRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementPeerRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.ProviderRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.QoSMeasurementObjectiveRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.SettingsRepository;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.BriefMeasurementResponseMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.FullMeasurementResponseMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapReportModelMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapTaskMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.MeasurementAgentMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.SettingsResponseMapper;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;

public class CouchDbStorageServiceMockitTest {
	
	private final static String MEASUREMENT_AGENT_UUID = "8e31f4ea-f92b-49d0-9486-55d4044037b3";
	
	private final static String SYSTEM_UUID = "d51260f5-cbdb-4d62-b32f-825d4238df47";
	
	private @Tested(fullyInitialized = true) CouchDbStorageService couchDbStorageService;
	
	@Mocked @Injectable
	private MeasurementRepository measurementRepository;
	
	@Mocked @Injectable
	private SettingsRepository settingsRepository;
	
	@Mocked @Injectable
	private MeasurementAgentRepository measurementAgentRepository;
	
	@Mocked @Injectable
	private QoSMeasurementObjectiveRepository qosMeasurementObjectiveRepository;
	
	@Mocked @Injectable
	private MeasurementPeerRepository measurementServerRepository;
	
	@Mocked @Injectable
	private EmbeddedNetworkTypeRepository embeddedNetworkTypeRepository;
	
	@Mocked @Injectable
	private DetailMeasurementService detailMeasurementService;
	
	@Mocked @Injectable
	private QoSEvaluationService qosEvaluationService;
	
	@Mocked @Injectable
	private LmapReportModelMapper lmapReportModelMapper;
	
	@Mocked @Injectable
	private MeasurementAgentMapper measurementAgentMapper;
	
	@Mocked @Injectable
	private SettingsResponseMapper settingsResponseMapper;
	
	@Mocked @Injectable
	private FullMeasurementResponseMapper fullMeasurementResponseMapper;
	
	@Mocked @Injectable
	private BriefMeasurementResponseMapper briefMeasurementResponseMapper;
	
	@Mocked @Injectable
	private LmapTaskMapper lmapTaskMapper;

	@Mocked @Injectable
	private ProviderRepository providerRepository;

	private LmapReportDto lmapReportDto; 
	
	private MeasurementAgent measurementAgent;
	
	String measurementUuid;
	
	String openDataUuid;
	
	String userAgentUuid;
	
	@Before
	public void init() {
		
		lmapReportDto = new LmapReportDto();
		final ApiRequestInfo info = new ApiRequestInfo();
		lmapReportDto.setAdditionalRequestInfo(info);
		info.setAgentId(MEASUREMENT_AGENT_UUID);
		info.setAgentType(MeasurementAgentTypeDto.MOBILE);
		
		lmapReportDto.setAgentId(MEASUREMENT_AGENT_UUID);
		
		measurementAgent = new MeasurementAgent();
		measurementAgent.setUuid(MEASUREMENT_AGENT_UUID);
		measurementAgent.setTermsAndConditionsAccepted(true);
		measurementAgent.setTermsAndConditionsAcceptedVersion(2);
		measurementAgent.setType(MeasurementAgentType.MOBILE);
		
	}
	
	@Test(expected = StorageServiceException.class)
	public void saveWithInvalidAgentUuidTest_ThrowsStorageServiceException () {
		lmapReportDto.setAgentId(null);
		couchDbStorageService.save(lmapReportDto);
	}
	
	@Test
	public void saveValidTest_callsSaveAndReturnsUuids () {
		
		new Expectations() {{
			measurementRepository.save((Measurement) any);
			times = 1;
			result = new Delegate<Measurement>() {
				public void delegate(Measurement measurement) {
					measurementUuid = measurement.getUuid();
					openDataUuid = measurement.getOpenDataUuid();
				}
			};
			
			measurementAgentRepository.findByUuid(anyString);
			result = measurementAgent;
			
			lmapReportModelMapper.map((LmapReportDto) any);
			result = new Measurement();
			
		}};
		
		final MeasurementResultResponse response = couchDbStorageService.save(lmapReportDto);
		
		assertEquals("invalid uuid returned", measurementUuid, response.getUuid());
		assertEquals("invalid uuid returned", openDataUuid, response.getOpenDataUuid());
	}
	
	@Test
	public void saveValidTestWithSystemUuid_callsSaveReturnsUuidsAndStoresCorrectSystemUuid () {
		
		new Expectations() {{
			measurementRepository.save((Measurement) any);
			times = 1;
			result = new Delegate<Measurement>() {
				public void delegate(Measurement measurement) {
					measurementUuid = measurement.getUuid();
					openDataUuid = measurement.getOpenDataUuid();
					assertEquals("System uuid not forwarded correctly", SYSTEM_UUID, measurement.getSystemUuid());
				}
			};
			
			measurementAgentRepository.findByUuid(anyString);
			result = measurementAgent;
			
			lmapReportModelMapper.map((LmapReportDto) any);
			result = new Measurement();
			
		}};
		
		final MeasurementResultResponse response = couchDbStorageService.save(lmapReportDto, SYSTEM_UUID);
		
		assertEquals("invalid uuid returned", measurementUuid, response.getUuid());
		assertEquals("invalid uuid returned", openDataUuid, response.getOpenDataUuid());
	}
	
	@Test
	public void isValidMeasurmeentAgentUuidTest() {
		final MeasurementAgent agentWithoutTermsAccepted = new MeasurementAgent();
		agentWithoutTermsAccepted.setTermsAndConditionsAccepted(false);
		
		new Expectations() {{
			measurementAgentRepository.findByUuid(anyString);
			returns(null, agentWithoutTermsAccepted, measurementAgent);
		}};
		
		assertFalse("Null agent not invalid", couchDbStorageService.isValidMeasurementAgentUuid(MEASUREMENT_AGENT_UUID));
		assertFalse("Agent without accepted terms not invalid", couchDbStorageService.isValidMeasurementAgentUuid(MEASUREMENT_AGENT_UUID));
		assertTrue("Valid agent falsly denied", couchDbStorageService.isValidMeasurementAgentUuid(MEASUREMENT_AGENT_UUID));
	}
	
	@Test
	public void registerNewMeasurementAgent_shouldReturnValidRegistrationResponse() {
		final RegistrationRequest req = new RegistrationRequest();
		req.setTermsAndConditionsAccepted(true);
		req.setTermsAndConditionsAcceptedVersion(1);
		
		new Expectations() {{
			
			measurementAgentMapper.map((ApiRequest<RegistrationRequest>) any);
			result = new MeasurementAgent();
			
			measurementAgentRepository.save((MeasurementAgent) any);
			times = 1;
			result = new Delegate<MeasurementAgent>() {
				public void delegate(MeasurementAgent agent) {
					userAgentUuid = agent.getUuid();
				}
			};
		}};
		
		
		final RegistrationResponse response = couchDbStorageService.registerMeasurementAgent(new ApiRequest<>(req));
		assertEquals("Invalid useragent uuid set", userAgentUuid, response.getAgentUuid());
	}
	
	@Test
	public void updateMeasurementAgent_shouldReturnValidRegistrationResponse() {
		final RegistrationRequest req = new RegistrationRequest();
		req.setTermsAndConditionsAccepted(true);
		req.setTermsAndConditionsAcceptedVersion(5);
		
		final ApiRequest<RegistrationRequest> apiRequest = new ApiRequest<>(req);
		final ApiRequestInfo reqInfo = new ApiRequestInfo();
		reqInfo.setAgentId(MEASUREMENT_AGENT_UUID);
		apiRequest.setRequestInfo(reqInfo);
		
		MeasurementAgent ret = new MeasurementAgent();
		ret.setUuid(MEASUREMENT_AGENT_UUID);
		ret.setTermsAndConditionsAcceptedVersion(5);
		
		new Expectations() {{
			
			measurementAgentMapper.map((ApiRequest<RegistrationRequest>) any);
			result = new Delegate<MeasurementAgent>() {
				public MeasurementAgent delegate(ApiRequest<RegistrationRequest> request) {
					MeasurementAgent ret = new MeasurementAgent();
					ret.setUuid(MEASUREMENT_AGENT_UUID);
					ret.setTermsAndConditionsAcceptedVersion(request.getData().getTermsAndConditionsAcceptedVersion());
					return ret;
				}
			};
					
			measurementAgentRepository.findByUuid(MEASUREMENT_AGENT_UUID);
			result = measurementAgent;
			
			measurementAgentRepository.save((MeasurementAgent) any);
			times = 1;
			result = new Delegate<MeasurementAgent>() {
				public void delegate(MeasurementAgent agent) {
					assertEquals("Did not update terms version", 5, agent.getTermsAndConditionsAcceptedVersion());
					userAgentUuid = agent.getUuid();
				}
			};
		}};
		
		
		final RegistrationResponse response = couchDbStorageService.registerMeasurementAgent(apiRequest);
		assertEquals("Invalid useragent uuid set", userAgentUuid, response.getAgentUuid());
	}

}
