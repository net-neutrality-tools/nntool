package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse.TranslatedQoSTypeInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SubMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse.SpeedMeasurementPeer;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nettest.shared.nntool.Helperfunctions;
import at.alladin.nettest.shared.server.helper.IpAddressMatcher;
import at.alladin.nettest.shared.server.service.GroupedMeasurementService;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ComputedNetworkPointInTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ConnectionInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Device;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.DeviceInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.EmbeddedNetworkType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.EmbeddedProvider;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MccMnc;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServer;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NatType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NatTypeInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkMobileInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkPointInTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Provider;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ProviderInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSResult;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QosAdvancedEvaluation;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QosBlockedPorts;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QosBlockedPorts.QosBlockedPortType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.RoamingType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.QoSMeasurementSettings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SubMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.DeviceRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.EmbeddedNetworkTypeRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementAgentRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementPeerRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.QoSMeasurementObjectiveRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.SettingsRepository;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.BriefMeasurementResponseMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.FullMeasurementResponseMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapReportModelMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapTaskMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.MeasurementAgentMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.SettingsResponseMapper;
import at.alladin.nntool.shared.qos.TcpResult;
import at.alladin.nntool.shared.qos.TracerouteResult;
import at.alladin.nntool.shared.qos.TracerouteResult.PathElement;
import at.alladin.nntool.shared.qos.UdpResult;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class CouchDbStorageService implements StorageService {
	
	private final Logger logger = LoggerFactory.getLogger(CouchDbStorageService.class);

	@Autowired
	private MeasurementRepository measurementRepository;
	
	@Autowired
	private SettingsRepository settingsRepository;
	
	@Autowired
	private MeasurementAgentRepository measurementAgentRepository;
	
	@Autowired
	private DeviceRepository deviceRepository; 
	
	@Autowired
	private QoSMeasurementObjectiveRepository qosMeasurementObjectiveRepository;
	
	@Autowired
	private MeasurementPeerRepository measurementPeerRepository;
	
	@Autowired
	private EmbeddedNetworkTypeRepository embeddedNetworkTypeRepository;
	
	@Autowired
	private QoSEvaluationService qosEvaluationService;
	
	@Autowired
	private GroupedMeasurementService groupedMeasurementService;
	
	@Autowired
	private LmapReportModelMapper lmapReportModelMapper;
	
	@Autowired
	private MeasurementAgentMapper measurementAgentMapper;
	
	@Autowired
	private SettingsResponseMapper settingsResponseMapper;
	
	@Autowired
	private FullMeasurementResponseMapper fullMeasurementResponseMapper;
	
	@Autowired
	private BriefMeasurementResponseMapper briefMeasurementResponseMapper;
	
	@Autowired
	private LmapTaskMapper lmapTaskMapper;
	
	@Autowired
	private ProviderService providerService;
	
	@Autowired
	private ObjectMapper objectMapper;

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.service.storage.v1.StorageService#save(at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto)
	 */
	@Override
	public MeasurementResultResponse save(LmapReportDto lmapReportDto, String systemUuid) throws StorageServiceException {
		final String agentUuid = lmapReportDto.getAgentId();
		if (agentUuid == null || !isValidMeasurementAgentUuid(agentUuid)) {
			throw new StorageServiceException("Invalid user agent id");
		}

		final Measurement measurement = lmapReportModelMapper.map(lmapReportDto);

		measurement.setSystemUuid(systemUuid);
		measurement.setUuid(UUID.randomUUID().toString());
		measurement.setOpenDataUuid(UUID.randomUUID().toString());
		measurement.setSubmitTime(LocalDateTime.now(ZoneId.of("UTC")));

		if (measurement.getNetworkInfo() != null) {
			final ComputedNetworkPointInTime cpit = computeNetworkInfo(measurement);
			
			if (cpit != null) {
				cpit.setNetworkMobileInfo(computeMobileInfoAndProcessMccMnc(measurement));
				cpit.setNatTypeInfo(computeNatType(measurement, cpit));
				cpit.setProviderInfo(computeProviderInfo(measurement, cpit));
				measurement.getNetworkInfo().setComputedNetworkInfo(cpit);
			}
			
			measurement.getNetworkInfo().getNetworkPointsInTime().forEach(networkPoint -> {
				if (networkPoint.getNetworkType() != null) {
					final EmbeddedNetworkType dbNetworkType = getNetworkTypeById(networkPoint.getNetworkType().getNetworkTypeId());
					if (dbNetworkType != null) {
						networkPoint.setNetworkType(dbNetworkType);
					}
				}
			});
		}
		
		if (measurement.getDeviceInfo() != null) {
			final DeviceInfo di = measurement.getDeviceInfo();
			final Device device = deviceRepository.findByCodeName(di.getCodeName());
			if (device != null) {
				di.setFullName(device.getFullname());
			}
		}

		calculateTotalMeasurementPayload(measurement);
		
		//add speed server (if not already present)
		final Map<MeasurementTypeDto, SubMeasurement> measurements = measurement.getMeasurements();
		if (measurements != null && measurements.containsKey(MeasurementTypeDto.SPEED)) {
			if (lmapReportDto != null && lmapReportDto.getResults() != null && lmapReportDto.getResults().size() > 0) {
				SpeedMeasurementResult lmapResult = null;
				for (SubMeasurementResult resultDto : lmapReportDto.getResults().get(0).getResults()) {
					if (resultDto instanceof SpeedMeasurementResult) {
						lmapResult = (SpeedMeasurementResult) resultDto;
						break;
					}
				}
				
				if (lmapResult != null && lmapResult.getConnectionInfo() != null && lmapResult.getConnectionInfo().getIdentifier() != null) {
					final SpeedMeasurement result = (SpeedMeasurement) measurement.getMeasurements().get(MeasurementTypeDto.SPEED);
					if (result.getConnectionInfo() == null) {
						result.setConnectionInfo(new ConnectionInfo());
					}
					final MeasurementServer server = measurementPeerRepository.findByPublicIdentifier(lmapResult.getConnectionInfo().getIdentifier());
					if (server != null) {
						result.getConnectionInfo().setIpAddress(server.getAddressIpv4());
						result.getConnectionInfo().setPort(server.getPortTls() != null ? server.getPortTls() : server.getPort());
					}
				}
			}
		}
		
    doAdvancedQosEvaluations(measurement);

		try {
			measurementRepository.save(measurement);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}

		final MeasurementResultResponse resultResponse = new MeasurementResultResponse();

		resultResponse.setUuid(measurement.getUuid());
		resultResponse.setOpenDataUuid(measurement.getOpenDataUuid());

		return resultResponse;
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.service.storage.v1.StorageService#save(at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto)
	 */
	@Override
	public MeasurementResultResponse save(LmapReportDto lmapReportDto) throws StorageServiceException {
		return save(lmapReportDto, null);
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.service.storage.v1.StorageService#registerMeasurementAgent(at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest)
	 */
	@Override
	public RegistrationResponse registerMeasurementAgent(ApiRequest<RegistrationRequest> registrationRequest) throws StorageServiceException {
		final RegistrationResponse ret = new RegistrationResponse();
		final MeasurementAgent agent = measurementAgentMapper.map(registrationRequest);
		
		if (agent.getUuid() != null) {
			final MeasurementAgent dbAgent;
			try {
				dbAgent = measurementAgentRepository.findByUuid(agent.getUuid());
			} catch (Exception ex) {
				throw new StorageServiceException("invalid uuid");
			}
			if (dbAgent == null) {
				throw new StorageServiceException("invalid uuid");
			}
			ret.setAgentUuid(dbAgent.getUuid());
			if (dbAgent.getTermsAndConditionsAcceptedVersion() < agent.getTermsAndConditionsAcceptedVersion()) {
				dbAgent.setTermsAndConditionsAcceptedVersion(agent.getTermsAndConditionsAcceptedVersion());
				dbAgent.setTermsAndConditionsAcceptedTime(LocalDateTime.now(ZoneId.of("UTC")));
				try {
					measurementAgentRepository.save(dbAgent);
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new StorageServiceException();
				}
			}
		} else {
			final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
			agent.setUuid(UUID.randomUUID().toString());
			agent.setTermsAndConditionsAcceptedTime(now);
			agent.setRegistrationTime(now);
			try {
				measurementAgentRepository.save(agent);
			} catch (Exception ex) {
				throw new StorageServiceException();
			}
			ret.setAgentUuid(agent.getUuid());
		}
		
		return ret;
	}
	
	@Override
	public boolean isValidMeasurementAgentUuid(String measurementAgentUuid) throws StorageServiceException {
		final MeasurementAgent agent;
		try {
			agent = measurementAgentRepository.findByUuid(measurementAgentUuid);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		if (agent == null || !agent.isTermsAndConditionsAccepted()) {
			return false;
		}
		return true;
	}
	
	@Override
	public LmapTaskDto getTaskDto(final MeasurementTypeDto type, final LmapCapabilityTaskDto capability, final String settingsUuid) {
		try {
			final Settings settings = settingsRepository.findByUuid(settingsUuid);
			
			switch (type) {
			case SPEED:
				final SpeedMeasurementSettings speedSettings = (SpeedMeasurementSettings) settings.getMeasurements().get(type);
				MeasurementServer server = null;
				if (capability != null && capability.getSelectedMeasurementPeerIdentifier() != null) {
					try {
						server = measurementPeerRepository.findByPublicIdentifier(capability.getSelectedMeasurementPeerIdentifier());
					} catch (Exception ex) {
						System.out.println(String.format("Failure obtaining requested measurement peer %s. Falling back to default peer.",
								capability.getSelectedMeasurementPeerIdentifier()));
						ex.printStackTrace();
					}
				}
				if (server == null) {
					server = measurementPeerRepository.findByUuid(speedSettings.getSpeedMeasurementServerUuid());
				}
				//TODO: load balancing needs to select correct measurement server
				final LmapTaskDto ret = lmapTaskMapper.map(settings, server, type.toString());
				return ret;
			case QOS:
				final List<QoSMeasurementObjective> qosObjectiveList = qosMeasurementObjectiveRepository.findAllByEnabled(true);
				
				if (settings.getMeasurements() != null && settings.getMeasurements().containsKey(MeasurementTypeDto.QOS)) {
					final QoSMeasurementSettings qosSettings = (QoSMeasurementSettings) settings.getMeasurements().get(MeasurementTypeDto.QOS);
					return lmapTaskMapper.map(settings, measurementPeerRepository.findByUuid(
							qosSettings.getQosServerUuid()), qosObjectiveList, type.toString());
				} else {
					return lmapTaskMapper.map(settings, null, qosObjectiveList, type.toString());
				}
				
			default:
				return null;
			}
			
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
	}
	
	@Override
	public SettingsResponse getSettings(String settingsUuid) throws StorageServiceException {
		SettingsResponse ret = null;
		try {
			ret = settingsResponseMapper.map(settingsRepository.findByUuid(settingsUuid));
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		ret.setQosTypeInfo(new LinkedHashMap<>());
		for (QosMeasurementType type : QosMeasurementType.values()) {
			final TranslatedQoSTypeInfo qosInfo = new TranslatedQoSTypeInfo();
			qosInfo.setName(type.getNameKey()); //TODO: Messagesource
			qosInfo.setDescription(type.getDescriptionKey()); //TODO: Messagesource
			try {
				ret.getQosTypeInfo().put(QoSMeasurementTypeDto.valueOf(type.getValue().toUpperCase()),  qosInfo);				
			} catch (Exception ex) {
				logger.error("Trying to map invalid enum w/value: " + type.getValue());
				ex.printStackTrace();
			}
		}
		return ret;
	}
	
	@Override
	public Page<BriefMeasurementResponse> getPagedBriefMeasurementResponseByAgentUuid(final String measurementAgentUuid, 
			final Pageable pageable) {
		return measurementRepository
			.findByAgentInfoUuidOrderByMeasurementTimeStartTimeDesc(measurementAgentUuid, pageable)
			.map(briefMeasurementResponseMapper::map);
	}
	
	@Override
	public FullMeasurementResponse getFullMeasurementByAgentAndMeasurementUuid(String measurementAgentUuid,
			String measurementUuid, Locale locale) throws StorageServiceException {
		final Measurement measurement = obtainMeasurement(measurementAgentUuid, measurementUuid);
		
		final FullMeasurementResponse ret = fullMeasurementResponseMapper.map(measurement);
		
		//evaluate the QoS stuff 
		final QoSMeasurement qosMeasurement = (QoSMeasurement) measurement.getMeasurements().get(MeasurementTypeDto.QOS);
		if (qosMeasurement != null) {
			final FullQoSMeasurement fullQosMeasurement = qosEvaluationService.evaluateQoSMeasurement(qosMeasurement, locale);
			ret.getMeasurements().put(MeasurementTypeDto.QOS, fullQosMeasurement);
		}
		
		return ret;
	}
	
	@Override
	public DetailMeasurementResponse getDetailMeasurementByAgentAndMeasurementUuid(String measurementAgentUuid, String measurementUuid, final String settingsUuid, final Locale locale) throws StorageServiceException {
		final Measurement measurement = obtainMeasurement(measurementAgentUuid, measurementUuid);
		//TODO: default settings?
		final Settings settings = settingsRepository.findByUuid(settingsUuid); // TODO: cache
		
		final FullMeasurementResponse fullMeasurementResponse = fullMeasurementResponseMapper.map(measurement);
		
		return groupedMeasurementService.groupResult(fullMeasurementResponse, settings.getSpeedtestDetailGroups(),
				locale, 10000);
	}
	
	@Override
	public DisassociateResponse disassociateMeasurement(final String agentUuid, final String measurementUuid) throws StorageServiceException {
		final Measurement measurement;
		try {
			measurement = measurementRepository.findByUuid(measurementUuid);
		} catch (Exception ex) {
			throw new StorageServiceException("Unknown measurement");
		}
		if (measurement.getAgentInfo().getUuid() == null) {
			throw new StorageServiceException("Measurement already disassociated");
		}
		if (!agentUuid.equals(measurement.getAgentInfo().getUuid())) {
			throw new StorageServiceException("Invalid agent/measurement uuid pair");
		}
		disassociateMeasurement(measurement);
		try {
			measurementRepository.save(measurement);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		return new DisassociateResponse();
	}
	
	@Override
	public DisassociateResponse disassociateAllMeasurements(final String agentUuid) throws StorageServiceException {
		try {
			final List<Measurement> measurementList = measurementRepository.findByAgentInfoUuid(agentUuid);
			measurementList.forEach(m -> disassociateMeasurement(m));
			measurementRepository.saveAll(measurementList);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		return new DisassociateResponse();
	}
	
	private void disassociateMeasurement (final Measurement toAnonymize) {
		if (toAnonymize.getAgentInfo() != null) {
			toAnonymize.getAgentInfo().setUuid(null);
		}
	}
	
	private Measurement obtainMeasurement (final String measurementAgentUuid, final String measurementUuid) throws StorageServiceException {
		final Measurement measurement;
		try {
			measurement = measurementRepository.findByUuid(measurementUuid);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		if (measurement == null || measurement.getAgentInfo() == null || measurementAgentUuid == null
				|| !measurementAgentUuid.equals(measurement.getAgentInfo().getUuid())) {
			throw new StorageServiceException("No measurement for agent and uuid found.");
		}
		return measurement;
	}
	
	public SpeedMeasurementPeerResponse getSpeedMeasurementPeers(ApiRequest<SpeedMeasurementPeerRequest> speedMeasurementPeerRequest) throws StorageServiceException {
		final List<MeasurementServer> peers = measurementPeerRepository.getAvailableSpeedMeasurementPeers();
		
		final SpeedMeasurementPeerResponse response = new SpeedMeasurementPeerResponse();
		
		response.setSpeedMeasurementPeers(
			peers
				.stream()
				.map(p -> {
					final SpeedMeasurementPeer sp = new SpeedMeasurementPeer();
					
					sp.setIdentifier(p.getPublicIdentifier());
					sp.setName(p.getName());
					sp.setDescription(p.getDescription());
					sp.setDefaultPeer(p.isDefaultPeer());
					
					return sp;
				})
				.collect(Collectors.toList())
		);
		
		return response;
	}
	
	private NetworkMobileInfo computeMobileInfoAndProcessMccMnc(final Measurement measurement) {
		NetworkMobileInfo computedNmi = null;
		
		if (measurement.getNetworkInfo() != null 
				&& measurement.getNetworkInfo().getNetworkPointsInTime() != null) {
			for (final NetworkPointInTime pit : measurement.getNetworkInfo().getNetworkPointsInTime()) {
				if (pit.getNetworkMobileInfo() != null) {
					final NetworkMobileInfo nmi = pit.getNetworkMobileInfo();
					if (computedNmi == null) {
						computedNmi = nmi;
					}

					final MccMnc networkMccMnc = pit.getNetworkMobileInfo().getNetworkOperatorMccMnc();
					final MccMnc simMccMnc = pit.getNetworkMobileInfo().getSimOperatorMccMnc();
					if (networkMccMnc == null || simMccMnc == null 
							|| nmi.getNetworkCountry() == null || nmi.getSimCountry() == null) {
						nmi.setRoaming(false);
						nmi.setRoamingType(RoamingType.NOT_AVAILABLE);
					}
					else {
						nmi.setRoaming(!networkMccMnc.equals(simMccMnc));
						if (nmi.getRoaming()) {
							nmi.setRoamingType(
									nmi.getNetworkCountry().equals(nmi.getSimCountry()) ? 
											RoamingType.NATIONAL : RoamingType.INTERNATIONAL);
						}
						else {
							nmi.setRoamingType(RoamingType.NO_ROAMING);
						}
					}
				}
			}
		}
		
		return computedNmi;
	}

	private void calculateTotalMeasurementPayload(final Measurement measurement) {
		if (measurement != null && measurement.getMeasurements() != null) {
			final SpeedMeasurement speedMeasurement = (SpeedMeasurement) measurement.getMeasurements().get(MeasurementTypeDto.SPEED);
			if (speedMeasurement != null) {
				final Long bytesDl = speedMeasurement.getBytesDownloadIncludingSlowStart();
				final Long bytesUl = speedMeasurement.getBytesUploadIncludingSlowStart();
				if (bytesDl != null && bytesUl != null) {
					final ConnectionInfo ci = speedMeasurement.getConnectionInfo();
					if (ci != null) {
						ci.setTcpPayloadTotalBytes(bytesUl + bytesDl);
					}
				}
			}
		}
	}

	private NatTypeInfo computeNatType(final Measurement measurement, final ComputedNetworkPointInTime cpit) {
		final NatType natType = cpit != null ?
				NatType.getNatType(cpit.getAgentPrivateIp(), cpit.getAgentPublicIp()) : NatType.NOT_AVAILABLE;

		final NatTypeInfo nat = new NatTypeInfo();
		nat.setIpVersion(Helperfunctions.getIpVersion(cpit.getAgentPrivateIp()));
		nat.setNatType(natType);
		nat.setIsBehindNat(natType != null
				&& !natType.equals(NatType.NOT_AVAILABLE)
				&& !natType.equals(NatType.NO_NAT));
		return nat;
	}

	private ComputedNetworkPointInTime computeNetworkInfo(final Measurement measurement) {
		ComputedNetworkPointInTime cpit = null;
		if (measurement.getNetworkInfo() != null) {
			List<NetworkPointInTime> npitList = measurement.getNetworkInfo().getNetworkPointsInTime();
			if (npitList != null && npitList.size() > 0) {
				final NetworkPointInTime npit = npitList.get(0);
				if (npit != null) {
					if (npit.getAgentPrivateIp() != null && npit.getAgentPublicIp() != null) {
						cpit = lmapReportModelMapper.map(npit);
					}
					if (npit.getNetworkType() != null) {
						final EmbeddedNetworkType dbNetworkType = getNetworkTypeById(npit.getNetworkType().getNetworkTypeId());
						if (cpit == null) {
							cpit = new ComputedNetworkPointInTime();
						}
						if (dbNetworkType != null) {
							cpit.setNetworkType(dbNetworkType);
						}
					}
				}
				
			}
		}
		
		if (measurement.getNetworkInfo().getComputedNetworkInfo() == null) {
			measurement.getNetworkInfo().setComputedNetworkInfo(new ComputedNetworkPointInTime());
		}
        
		return cpit;
	}
	
	private ProviderInfo computeProviderInfo(final Measurement measurement, final ComputedNetworkPointInTime cpit) {
		InetAddress clientAddress = null;
		try {
			clientAddress = InetAddress.getByName(cpit.getAgentPublicIp());
		}
		catch (final UnknownHostException e) {
			e.printStackTrace();
		}
		
		//reverse DNS:
        String reverseDNS = Helperfunctions.reverseDNSLookup(clientAddress);
        if (reverseDNS != null) {
        	//need to cut off last dot
        	reverseDNS = reverseDNS.replaceFirst("\\.$", "");
        }
        logger.debug("rDNS for: {} is: {}", clientAddress, reverseDNS);
        cpit.setPublicIpRdns(reverseDNS);

		//ASN:
        final Long asn = Helperfunctions.getASN(clientAddress);

        String asName = null;
        String asCountry = null;

        if (asn != null) {
            asName = Helperfunctions.getASName(asn);
            asCountry = Helperfunctions.getAScountry(asn);
        }

        final ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setCountryCodeAsn(asCountry);
        providerInfo.setPublicIpAsn(asn);
        providerInfo.setPublicIpAsName(asName);
        
        Provider provider = null;
               
        //Try mobile network info (mcc/mnc)
        final NetworkMobileInfo mobileInfo = cpit.getNetworkMobileInfo();
        if (mobileInfo != null) {
        	provider = providerService.getByMccMnc(mobileInfo.getSimOperatorMccMnc(), mobileInfo.getNetworkOperatorMccMnc(), measurement.getSubmitTime());
        }
        
        //Try ASN:
        if (provider == null && asn != null) {
	        provider = providerService.getByAsn(asn, reverseDNS);
        }
    	
        if (provider != null) {
        	final EmbeddedProvider embeddedProvider = new EmbeddedProvider();
	        embeddedProvider.setShortName(provider.getShortName());
	        embeddedProvider.setName(provider.getName());
	        providerInfo.setProvider(embeddedProvider);
        }

        return providerInfo;
	}
	
	private EmbeddedNetworkType getNetworkTypeById(final Integer networkTypeId) {
		if (networkTypeId == null) {
			return null;
		}
		final EmbeddedNetworkType dbNetworkType = 
				embeddedNetworkTypeRepository.findByNetworkTypeId(networkTypeId);
		if (dbNetworkType != null) {
			final EmbeddedNetworkType ret = new EmbeddedNetworkType();
			ret.setNetworkTypeId(dbNetworkType.getNetworkTypeId());
			ret.setCategory(dbNetworkType.getCategory());
			ret.setGroupName(dbNetworkType.getGroupName());
			ret.setName(dbNetworkType.getName());
			ret.setDocType(null);
			return ret;
		}
		return null;
	}

	/**
	 * does advanced evaluations on QoS results.
	 * @param measurement
	 */
	private void doAdvancedQosEvaluations(final Measurement measurement) {
		if (measurement.getMeasurements() == null) {
			return;
		}
		
		QoSMeasurement qosMeasurement = (QoSMeasurement) measurement.getMeasurements().get(MeasurementTypeDto.QOS);
		if (qosMeasurement == null) {
			return;
		}
		
		if (measurement.getNetworkInfo() != null) {

			//check for CGN by matching IP addresses from traceroute test (see RFC 6598)
			IpAddressMatcher matcher = new IpAddressMatcher("100.64.0.0/10");
			boolean isCgnCheckFinished = false;

			for (QoSResult qos : qosMeasurement.getResults()) {
				QosAdvancedEvaluation qosEval = measurement.getQosAdvancedEvaluation();
				if (qosEval == null) {
					measurement.setQosAdvancedEvaluation(new QosAdvancedEvaluation());
					qosEval = measurement.getQosAdvancedEvaluation();
				}
				
				if (!isCgnCheckFinished && QoSMeasurementType.TRACEROUTE.equals(qos.getType())) {
					TracerouteResult result = objectMapper.convertValue(qos.getResults(), TracerouteResult.class);
					if (result.getResultEntries() != null) {
						for (PathElement path : result.getResultEntries()) {
							if (matcher.matches(path.getHost())) {
								isCgnCheckFinished = true;
								measurement.getNetworkInfo().setIsCgnDetected(true);
								break;
							}
						}
					}
				}
				else if(QoSMeasurementType.TCP.equals(qos.getType())) {
					Map<QosBlockedPortType, QosBlockedPorts> map = qosEval.getBlockedPorts();
					if (map == null) {
						qosEval.setBlockedPorts(new HashMap<>());
						map = qosEval.getBlockedPorts();
					}
					
					final QosBlockedPorts blocked = new QosBlockedPorts();
					map.put(QosBlockedPortType.TCP, blocked);
					
					final TcpResult result = objectMapper.convertValue(qos.getResults(), TcpResult.class);

					final List<Integer> portInList = new ArrayList<>();
					final List<Integer> portOutList = new ArrayList<>();
					if (result.getInPort() != null) {
						if(!"OK".equals(result.getInResult())) {
							portInList.add(result.getInPort());
						}
					}
					
					if (result.getOutPort() != null) {
						if (!"OK".equals(result.getOutResult())) {
							portOutList.add(result.getOutPort());
						}
					}
					
					blocked.setInCount(portInList.size());
					blocked.setOutCount(portOutList.size());
					
					if (!portInList.isEmpty()) {
						blocked.setInPorts(portInList);
					}
					if (!portOutList.isEmpty()) {
						blocked.setOutPorts(portOutList);
					}
				}

			
				else if(QoSMeasurementType.UDP.equals(qos.getType())) {
					Map<QosBlockedPortType, QosBlockedPorts> map = qosEval.getBlockedPorts();
					if (map == null) {
						qosEval.setBlockedPorts(new HashMap<>());
						map = qosEval.getBlockedPorts();
					}

					final QosBlockedPorts blocked = new QosBlockedPorts();				
					map.put(QosBlockedPortType.UDP, blocked);

					final UdpResult result = objectMapper.convertValue(qos.getResults(), UdpResult.class);
					
					final List<Integer> portInList = new ArrayList<>();
					final List<Integer> portOutList = new ArrayList<>();
					if (result.getOutPort() != null && result.getResultOutNumPacketsResponse() != null) {
						try { 
							int packets = Integer.valueOf(result.getResultOutNumPacketsResponse().toString());
							if (packets == 0) {
								portOutList.add(Integer.valueOf(result.getOutPort().toString()));
							}
						}
						catch (final Exception e) { }
					}
					if (result.getInPort() != null) {
						try { 
							int packets = Integer.valueOf(result.getResultInNumPacketsResponse().toString());
							if (packets == 0) {
								portInList.add(Integer.valueOf(result.getInPort().toString()));
							}
						}
						catch (final Exception e) { }						
					}
					
					blocked.setInCount(portInList.size());
					blocked.setOutCount(portOutList.size());
					
					if (!portInList.isEmpty()) {
						blocked.setInPorts(portInList);
					}
					if (!portOutList.isEmpty()) {
						blocked.setOutPorts(portOutList);
					}
				}
			}
		}
	}
}
