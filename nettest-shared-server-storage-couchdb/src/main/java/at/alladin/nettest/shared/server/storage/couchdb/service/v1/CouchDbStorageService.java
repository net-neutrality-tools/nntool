package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
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
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse.SpeedMeasurementPeer;
import at.alladin.nettest.shared.nntool.Helperfunctions;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ComputedNetworkPointInTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ConnectionInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MccMnc;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServer;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NatType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NatTypeInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkMobileInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkPointInTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.RoamingType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.QoSMeasurementSettings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;
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

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class CouchDbStorageService implements StorageService {

	@Autowired
	private MeasurementRepository measurementRepository;
	
	@Autowired
	private SettingsRepository settingsRepository;
	
	@Autowired
	private MeasurementAgentRepository measurementAgentRepository;
	
	@Autowired
	private QoSMeasurementObjectiveRepository qosMeasurementObjectiveRepository;
	
	@Autowired
	private MeasurementPeerRepository measurementPeerRepository;
	
	@Autowired
	private QoSEvaluationService qosEvaluationService;
	
	@Autowired
	private DetailMeasurementService detailMeasurementService;
	
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
				measurement.getNetworkInfo().setComputedNetworkInfo(cpit);
			}
		}

		calculateTotalMeasurementPayload(measurement);

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
		try {
			return settingsResponseMapper.map(settingsRepository.findByUuid(settingsUuid));
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
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
		final Settings settings = settingsRepository.findByUuid(settingsUuid);
		return detailMeasurementService.groupResult(measurement, settings.getSpeedtestDetailGroups(),
				Locale.ENGLISH, 10000);//detailMeasurementResponseMapper.map(measurement);
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
				NatType.getNatType(cpit.getClientPrivateIp(), cpit.getClientPublicIp()) : NatType.NOT_AVAILABLE;

		final NatTypeInfo nat = new NatTypeInfo();
		nat.setIpVersion(Helperfunctions.getIpVersion(cpit.getClientPrivateIp()));
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
				if (npit != null && npit.getClientPrivateIp() != null && npit.getClientPublicIp() != null) {
					cpit = lmapReportModelMapper.map(npit);
				}
			}
		}

		if (measurement.getNetworkInfo().getComputedNetworkInfo() == null) {
			measurement.getNetworkInfo().setComputedNetworkInfo(new ComputedNetworkPointInTime());
		}

		return cpit;
	}
}
