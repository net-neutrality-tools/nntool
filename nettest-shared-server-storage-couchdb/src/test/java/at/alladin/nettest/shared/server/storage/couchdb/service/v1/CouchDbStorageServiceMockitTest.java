package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.server.helper.ReturnCodeMessageSource;
import at.alladin.nettest.shared.server.service.GroupedMeasurementService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgentType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.DeviceRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServer;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
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
	
	private final static String MEASUREMENT_UUID = "98ffd8da-d571-4a3b-b87c-ec783f883b07";

	private final static String MEASUREMENT_AGENT_UUID = "8e31f4ea-f92b-49d0-9486-55d4044037b3";
	
	private final static String SETTINGS_UUID = "4a9b9ccb-3d33-4d7a-94cc-67eef1a4e23b";

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
	private GroupedMeasurementService groupedMeasurementService;
	
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

	@Mocked @Injectable
	private DeviceRepository deviceRepository;
	
	@Mocked @Injectable
	private ReturnCodeMessageSource messageSource;

	private LmapReportDto lmapReportDto;
	
	private MeasurementAgent measurementAgent;
	
	private Measurement measurement;
	
	private String measurementUuid;
	
	private String openDataUuid;

	private String userAgentUuid;
	
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
		
		measurement = new Measurement();
		measurement.setUuid(MEASUREMENT_UUID);
		measurement.setSystemUuid(SYSTEM_UUID);
		MeasurementAgentInfo agentInfo = new MeasurementAgentInfo();
		agentInfo.setUuid(MEASUREMENT_AGENT_UUID);
		agentInfo.setType(MeasurementAgentType.MOBILE);
		measurement.setAgentInfo(agentInfo);

	}
	
	@Test(expected = StorageServiceException.class)
	public void saveWithInvalidAgentUuidTest_ThrowsStorageServiceException() {
		lmapReportDto.setAgentId(null);
		couchDbStorageService.save(lmapReportDto);
	}
	
	@Test
	public void saveValidTest_callsSaveAndReturnsUuids() {
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
	public void saveValidTestWithSystemUuid_callsSaveReturnsUuidsAndStoresCorrectSystemUuid() {
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

	@Test
	public void getTaskDtoForSpeedTask_shouldProvideValidValuesForMapper() {

		final Settings settings = new Settings();
		settings.setMeasurements(new HashMap<>());
		settings.getMeasurements().put(MeasurementTypeDto.SPEED, settings.new SpeedMeasurementSettings());
		settings.setId(SETTINGS_UUID);

		final LmapCapabilityTaskDto capability = new LmapCapabilityTaskDto();
		capability.setSelectedMeasurementPeerIdentifier("peer_id");

		final MeasurementServer server = new MeasurementServer();
		server.setName("peer");
		server.setPublicIdentifier("peer_id");

		new Expectations() {{

			settingsRepository.findByUuid(SETTINGS_UUID);
			result = settings;

			measurementServerRepository.findByPublicIdentifier("peer_id");
			result = server;

			lmapTaskMapper.map((Settings) any, (MeasurementServer) any, anyString, false);
			result = new Delegate() {
				public LmapTaskDto delegate(Settings settings, MeasurementServer server, String type, boolean useIPv6) {
					assertEquals("unexpected settings provided to mapper", SETTINGS_UUID, settings.getId());
					assertEquals("unexpected server provided to mapper", "peer", server.getName());
					assertEquals("unexpected server provided to mapper", "peer_id", server.getPublicIdentifier());
					assertEquals("unexpected type provided to mapper", "SPEED", type);
					assertEquals("unexpected useIPv6 value provided to mapper", false, useIPv6);
					return new LmapTaskDto();
				}
			};
		}};

		assertNotNull("Invalid object returned", couchDbStorageService.getTaskDto(MeasurementTypeDto.SPEED, capability, SETTINGS_UUID, false));
	}

	@Test(expected = StorageServiceException.class)
	public void disassociateMeasurementCallWithInvalidUuid_throwsStorageServiceException() {

		new Expectations() {{
			measurementRepository.findByUuid(MEASUREMENT_UUID);
			result = new RuntimeException();
		}};

		couchDbStorageService.disassociateMeasurement(MEASUREMENT_AGENT_UUID, MEASUREMENT_UUID);
	}

	@Test(expected = StorageServiceException.class)
	public void disassociateMeasurementCallWhichIsAlreadyDisassociated_throwsStorageServiceException() {

		measurement.getAgentInfo().setUuid(null);

		new Expectations() {{
			measurementRepository.findByUuid(MEASUREMENT_UUID);
			result = measurement;
		}};

		couchDbStorageService.disassociateMeasurement(MEASUREMENT_AGENT_UUID, MEASUREMENT_UUID);
	}

	@Test(expected = StorageServiceException.class)
	public void disassociateMeasurementCallWhichIsNotFromTheProvidedUserAgent_throwsStorageServiceException() {

		measurement.getAgentInfo().setUuid("invalid uuid");

		new Expectations() {{
			measurementRepository.findByUuid(MEASUREMENT_UUID);
			result = measurement;
		}};

		couchDbStorageService.disassociateMeasurement(MEASUREMENT_AGENT_UUID, MEASUREMENT_UUID);
	}

	@Test
	public void disassociateMeasurementCall_correctedMeasurementIsForwardedToRepo() {

		new Expectations() {{
			measurementRepository.findByUuid(MEASUREMENT_UUID);
			result = measurement;

			measurementRepository.save((Measurement) any);
			result = new Delegate() {
				public Measurement delegate(Measurement measurement) {
					assertNull("Measurement not correctly disassociated", measurement.getAgentInfo().getUuid());
					return measurement;
				}
			};
		}};

		final DisassociateResponse response = couchDbStorageService.disassociateMeasurement(MEASUREMENT_AGENT_UUID, MEASUREMENT_UUID);
		assertNotNull("Null response returned from disassociation", response);
	}

	@Test
	public void disassociatedAllMeasurements_correctlyDissassociatesAllMeasurementsOfTheAgent() {
		final Measurement secondMeasurement = new Measurement();
		secondMeasurement.setUuid("e9e72f9f-f33b-49fe-a1fa-e8a664235afd");
		secondMeasurement.setSystemUuid(SYSTEM_UUID);
		MeasurementAgentInfo agentInfo = new MeasurementAgentInfo();
		agentInfo.setUuid(MEASUREMENT_AGENT_UUID);
		agentInfo.setType(MeasurementAgentType.MOBILE);
		secondMeasurement.setAgentInfo(agentInfo);

		final List<Measurement> measurementList = new ArrayList<>();
		measurementList.add(measurement);
		measurementList.add(secondMeasurement);

		new Expectations() {{
			measurementRepository.findByAgentInfoUuid(MEASUREMENT_AGENT_UUID);
			result = measurementList;

			measurementRepository.saveAll((List<Measurement>) any);
			result = new Delegate() {
				public Iterable<Measurement> delegate(List<Measurement> measurementList) {
					assertEquals("Unexpected measurement count during save", 2, measurementList.size());
					assertNull("Measurement one not successfully disassociated", measurementList.get(0).getAgentInfo().getUuid());
					assertNull("Measurement two not successfully disassociated", measurementList.get(1).getAgentInfo().getUuid());
					return null;
				}
			};
		}};

		couchDbStorageService.disassociateAllMeasurements(MEASUREMENT_AGENT_UUID);
	}

}
