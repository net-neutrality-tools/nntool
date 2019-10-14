package at.alladin.nettest.service.collector.service;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.service.collector.config.CollectorServiceProperties;
import at.alladin.nettest.service.collector.helper.PreparedStatementWrapper;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.DeviceInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.FullRttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkPointInTimeInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.OperatingSystemInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;
import at.alladin.nettest.shared.server.config.ElasticSearchProperties;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class MeasurementResultService {

	private static final Logger logger = LoggerFactory.getLogger(MeasurementResultService.class);
	
	private static final String INSERT_MEASUREMENT_SQL = "INSERT INTO measurements ("
		+ "open_data_uuid, agent_uuid, system_uuid, start_time, end_time, duration_ns, os_name, os_version, os_api_level, os_cpu_usage, os_mem_usage, device_codename, device_model, device_fullname, network_client_public_ip, "
		+ "provider_public_ip_asn, provider_public_ip_rdns, provider_public_ip_as_name, provider_country_asn, provider_name, provider_shortname, agent_app_version_name, agent_app_version_code, agent_language, agent_app_git_rev, "
		+ "agent_timezone, network_signal_info, mobile_network_operator_mcc, mobile_network_operator_mnc, mobile_network_country_code, mobile_network_operator_name, mobile_sim_operator_mcc, mobile_sim_operator_mnc, mobile_sim_operator_name, "
		+ "mobile_sim_country_code, mobile_is_roaming, mobile_roaming_type, initial_network_type_id, geo_location_accuracy, mobile_network_signal_strength_2g3g_dbm, mobile_network_lte_rsrp_dbm, mobile_network_lte_rsrq_db, "
		+ "mobile_network_lte_rssnr_db, wifi_network_link_speed_bps, wifi_network_rssi_dbm, network_group_name, network_client_public_ip_country_code, agent_type, geo_location_latitude, geo_location_longitude, geo_location_geometry, "
		+ "mobile_network_frequency, tag, wifi_initial_bssid, wifi_initial_ssid"
		+ ") VALUES ("
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "ST_Transform(ST_SetSRID(ST_MakePoint(?, ?), 4326), 900913), ?, ?, ?, ?"
		+ ")";
	
	private static final String INSERT_IAS_MEASUREMENT_SQL = "INSERT INTO ias_measurements ("
		+ "measurement_open_data_uuid, relative_start_time_ns, relative_end_time_ns, start_time, end_time, duration_ns, status, reason, version_protocol, version_library, implausible, throughput_avg_download_bps, throughput_avg_upload_bps, "
		+ "throughput_avg_download_log, throughput_avg_upload_log, bytes_download, bytes_upload, requested_duration_download_ns, requested_duration_upload_ns, duration_download_ns, duration_upload_ns, "
		+ "relative_start_time_download_ns, relative_start_time_upload_ns, duration_rtt_ns, connection_info, rtt_median_ns, rtt_median_log, speed_raw_data, rtt_info, "
		+ "requested_duration_download_slow_start_ns, requested_duration_upload_slow_start_ns"
		+ ") VALUES ("
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?"
		+ ")";
	
	private static final String INSERT_QOS_MEASUREMENT_SQL = "INSERT INTO qos_measurements ("
		+ "measurement_open_data_uuid, relative_start_time_ns, relative_end_time_ns, start_time, end_time, duration_ns, status, reason, version_protocol, version_library, implausible"
		+ ") VALUES ("
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?"
		+ ")";
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CollectorServiceProperties collectorServiceProperties;
	
	@Autowired
	private StorageService storageService;
	
	@Autowired(required = false)
	@Qualifier("elasticSearchClient")
	private RestHighLevelClient elasticSearchClient;
	
	@Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
	
	/**
	 * 
	 * @param lmapReportDto
	 * @return
	 * @throws StorageServiceException
	 */
	public MeasurementResultResponse saveResult(LmapReportDto lmapReportDto) throws StorageServiceException {
		final String systemUuid = collectorServiceProperties.getSystemUuid();
		
		// Store measurement result into CouchDB
		final MeasurementResultResponse resultResponse = storageService.save(lmapReportDto, systemUuid);
		
		final String uuid = resultResponse.getUuid();
		final String openDataUuid = resultResponse.getOpenDataUuid();
		
		logger.info("Saved result (uuid: {}, open_data_uuid: {})", uuid, openDataUuid);
		
		FullMeasurementResponse measurementDto = null;
		if (elasticSearchClient != null || jdbcTemplate != null) {
			final String agentUuid = lmapReportDto.getAgentId();
			
			// TODO: don't evaluate QoS the same way it is evaluated for users.
			measurementDto = storageService.getFullMeasurementByAgentAndMeasurementUuid(agentUuid, uuid, Locale.ENGLISH);
			
			if (measurementDto != null) {
				// Store measurement result in Elasticsearch if available
				if (elasticSearchClient != null) {
					try {
						storeMeasurementInElasticsearch(measurementDto);
						logger.info("Saved result (uuid: {}, open_data_uuid: {}) in Elasticsearch", uuid, openDataUuid);
					} catch (Exception ex) {
						logger.error("Could not save result (uuid: {}, open_data_uuid: {}) in Elasticsearch", uuid, openDataUuid, ex);
					}
				}
				
				// Store measurement result in PostgreSQL if available
				if (jdbcTemplate != null) {
					try {
						storeMeasurementInPostgresql(measurementDto);
						logger.info("Saved result (uuid: {}, open_data_uuid: {}) in PostgreSQL", uuid, openDataUuid);
					} catch (Exception ex) {
						logger.error("Could not save result (uuid: {}, open_data_uuid: {}) in PostgreSQL", uuid, openDataUuid, ex);
					}
				}
			}
		}
		
		return resultResponse;
	}
	
	private void storeMeasurementInElasticsearch(FullMeasurementResponse measurementDto) throws Exception {
		final ElasticSearchProperties esp = collectorServiceProperties.getElasticsearch();

		@SuppressWarnings("unchecked")
		final IndexRequest indexRequest = new IndexRequest(esp.getIndex())
				.id(measurementDto.getOpenDataUuid())
				.source(objectMapper.convertValue(measurementDto, Map.class));
		
		try {
			final IndexResponse indexResponse = elasticSearchClient.index(indexRequest, RequestOptions.DEFAULT);
			
			logger.debug("IndexRequest response: {}", indexResponse);
		} catch (IOException ex) {
			throw ex;
		}
	}
	
	private void storeMeasurementInPostgresql(FullMeasurementResponse measurementDto) {
		final DeviceInfoDto deviceInfo = measurementDto.getDeviceInfo();
		final OperatingSystemInfoDto osInfo = deviceInfo.getOsInfo();
		final NetworkInfoDto networkInfo = measurementDto.getNetworkInfo();
		final MeasurementAgentInfoDto agentInfo = measurementDto.getAgentInfo();
		
		jdbcTemplate.update(INSERT_MEASUREMENT_SQL, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				final PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(ps);
				
				psWrapper.setAsUuid(1, measurementDto.getOpenDataUuid());
				psWrapper.setAsUuid(2, agentInfo.getUuid());
				psWrapper.setAsUuid(3, measurementDto.getSystemUuid());
				
				psWrapper.setJodaLocalDateTime(4, measurementDto.getStartTime());
				psWrapper.setJodaLocalDateTime(5, measurementDto.getEndTime());
				
				psWrapper.setLong(6, measurementDto::getDurationNs);
				psWrapper.setString(7, osInfo::getName);
				psWrapper.setString(8, osInfo::getVersion);
				psWrapper.setString(9, osInfo::getApiLevel);
				psWrapper.setObject(10, null); //osInfo.getCpu*
				psWrapper.setObject(11, null); //osInfo.getMemory*
				psWrapper.setString(12, deviceInfo::getCodeName);
				psWrapper.setString(13, deviceInfo::getModel);
				psWrapper.setString(14, deviceInfo::getFullName);
				
				psWrapper.setString(22, agentInfo::getAppVersionName);
				psWrapper.setInt(23, agentInfo::getAppVersionCode);
				psWrapper.setString(24, agentInfo::getLanguage);
				psWrapper.setString(25, agentInfo::getAppGitRevision);
				psWrapper.setString(26, agentInfo::getTimezone);
				
				ps.setObject(36, null); // ?
				ps.setObject(37, null); // ?
				
				psWrapper.setString(48, () -> { return agentInfo.getType().name(); });
				psWrapper.setString(54, measurementDto::getTag);
				
				final List<NetworkPointInTimeInfoDto> networkPointInTimeList = networkInfo.getNetworkPointInTimeInfo();
				if (networkPointInTimeList != null && networkPointInTimeList.size() > 0) {
					final NetworkPointInTimeInfoDto networkPointInTimeInfo = networkPointInTimeList.get(0);
					
					psWrapper.setString(15, networkPointInTimeInfo::getAgentPublicIp);
					psWrapper.setLong(16, networkPointInTimeInfo::getPublicIpAsn);
					psWrapper.setString(17, networkPointInTimeInfo::getPublicIpRdns);
					psWrapper.setString(18, networkPointInTimeInfo::getPublicIpAsName);
					psWrapper.setString(19, networkPointInTimeInfo::getCountryCodeAsn);
					psWrapper.setString(20, networkPointInTimeInfo::getProviderName);
					psWrapper.setString(21, networkPointInTimeInfo::getProviderShortName);
					psWrapper.setInt(28, () -> { return Integer.parseInt(networkPointInTimeInfo.getNetworkOperatorMccMnc().split("-")[0]); });
					psWrapper.setInt(29, () -> { return Integer.parseInt(networkPointInTimeInfo.getNetworkOperatorMccMnc().split("-")[1]); });
					psWrapper.setString(30, networkPointInTimeInfo::getNetworkCountry);
					psWrapper.setString(31, networkPointInTimeInfo::getNetworkOperatorName);
					psWrapper.setInt(32, () -> { return Integer.parseInt(networkPointInTimeInfo.getSimOperatorMccMnc().split("-")[0]); });
					psWrapper.setInt(33, () -> { return Integer.parseInt(networkPointInTimeInfo.getSimOperatorMccMnc().split("-")[1]); });
					psWrapper.setString(34, networkPointInTimeInfo::getSimOperatorName);
					psWrapper.setString(35, networkPointInTimeInfo::getSimCountry);
					
					psWrapper.setInt(38, networkPointInTimeInfo::getNetworkTypeId);
					psWrapper.setString(46, networkPointInTimeInfo::getNetworkTypeGroupName);
					psWrapper.setString(47, networkPointInTimeInfo::getAgentPublicIpCountryCode);
				} else {
					ps.setObject(15, null);
					ps.setObject(16, null);
					ps.setObject(17, null);
					ps.setObject(18, null);
					ps.setObject(19, null);
					ps.setObject(20, null);
					ps.setObject(21, null);
					ps.setObject(28, null);
					ps.setObject(29, null);
					ps.setObject(30, null);
					ps.setObject(31, null);
					ps.setObject(32, null);
					ps.setObject(33, null);
					ps.setObject(34, null);
					ps.setObject(35, null);
					ps.setObject(38, null);
					ps.setObject(46, null);
					ps.setObject(47, null);
				}
				
				final List<SignalDto> signalList = networkInfo.getSignals();
				if (signalList != null && signalList.size() > 0) {
					final SignalDto signalInfo = signalList.get(0);
					
					ps.setObject(27, null); //networkInfo.getSignals()
					
					psWrapper.setInt(40, signalInfo::getSignalStrength2g3gDbm);
					psWrapper.setInt(41, signalInfo::getLteRsrpDbm);
					psWrapper.setInt(42, signalInfo::getLteRsrqDb);
					psWrapper.setInt(43, signalInfo::getLteRssnrDb);
					psWrapper.setInt(44, signalInfo::getWifiLinkSpeedBps);
					psWrapper.setInt(45, signalInfo::getWifiRssiDbm);
					psWrapper.setObject(53, () -> { return signalInfo.getCellInfo().getFrequency(); });
					psWrapper.setString(55, signalInfo::getWifiBssid);
					psWrapper.setString(56, signalInfo::getWifiSsid);
				} else {
					ps.setObject(27, null);
					ps.setObject(40, null);
					ps.setObject(41, null);
					ps.setObject(42, null);
					ps.setObject(43, null);
					ps.setObject(44, null);
					ps.setObject(45, null);
					ps.setObject(53, null);
					ps.setObject(55, null);
					ps.setObject(56, null);
				}
				
				final List<GeoLocationDto> geoLocationList = measurementDto.getGeoLocations();
				if (geoLocationList != null && geoLocationList.size() > 0) {
					final GeoLocationDto geoLocation = geoLocationList.get(0);
					
					psWrapper.setDouble(39, geoLocation::getAccuracy);
					psWrapper.setDouble(49, geoLocation::getLatitude);
					psWrapper.setDouble(50, geoLocation::getLongitude); 
					psWrapper.setDouble(51, geoLocation::getLongitude);
					psWrapper.setDouble(52, geoLocation::getLatitude);
				} else {
					ps.setObject(39, null);
					ps.setObject(49, null);
					ps.setObject(50, null);
					ps.setObject(51, null);
					ps.setObject(52, null);
				}
			}
		});
		
		Map<MeasurementTypeDto, FullSubMeasurement> subMeasurements = measurementDto.getMeasurements();
		
		FullSubMeasurement iasSubMeasurement = subMeasurements.get(MeasurementTypeDto.SPEED);
		if (iasSubMeasurement instanceof FullSpeedMeasurement) {
			FullSpeedMeasurement iasMeasurement = (FullSpeedMeasurement) iasSubMeasurement;
			
			final FullRttInfoDto rttInfo = iasMeasurement.getRttInfo();
			Long l = iasMeasurement.getThroughputAvgDownloadBps();
			l = iasMeasurement.getThroughputAvgUploadBps();
			
			jdbcTemplate.update(INSERT_IAS_MEASUREMENT_SQL, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					final PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(ps);
					
					setSubMeasurementValues(psWrapper, measurementDto.getOpenDataUuid(), iasMeasurement);
					
					psWrapper.setLong(12, iasMeasurement::getThroughputAvgDownloadBps);
					psWrapper.setLong(13, iasMeasurement::getThroughputAvgUploadBps);
					psWrapper.setDouble(14, iasMeasurement::getThroughputAvgDownloadLog);
					psWrapper.setDouble(15, iasMeasurement::getThroughputAvgUploadLog);
					psWrapper.setLong(16, iasMeasurement::getBytesDownload);
					psWrapper.setLong(17, iasMeasurement::getBytesUpload);
					psWrapper.setLong(18, iasMeasurement::getRequestedDurationDownloadNs);
					psWrapper.setLong(19, iasMeasurement::getRequestedDurationUploadNs);
					psWrapper.setLong(20, iasMeasurement::getDurationNs);
					psWrapper.setLong(21, iasMeasurement::getDurationUploadNs);
					psWrapper.setLong(22, iasMeasurement::getRelativeStartTimeDownloadNs);
					psWrapper.setLong(23, iasMeasurement::getRelativeStartTimeUploadNs);
					psWrapper.setLong(24, iasMeasurement::getDurationRttNs);
					ps.setObject(25, null /*iasMeasurement.getConnectionInfo()*/);
					psWrapper.setLong(26, rttInfo::getMedianNs);
					psWrapper.setDouble(27, rttInfo::getMedianLog);
					ps.setObject(28, null /*iasMeasurement.getDownloadRawData()*/); // TODO: upload
					ps.setObject(29, null /*iasMeasurement.getRttInfo()*/);
					ps.setObject(30, null);
					ps.setObject(31, null);
					
				}
			});
		}
		
		FullSubMeasurement qosSubMeasurement = subMeasurements.get(MeasurementTypeDto.QOS);
		if (qosSubMeasurement instanceof FullQoSMeasurement) {
			FullQoSMeasurement qosMeasurement = (FullQoSMeasurement) qosSubMeasurement;
			
			jdbcTemplate.update(INSERT_QOS_MEASUREMENT_SQL, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					final PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(ps);
					
					setSubMeasurementValues(psWrapper, measurementDto.getOpenDataUuid(), qosMeasurement);
				}
			});
			
			// TODO: qos_measurement_results and qos_objectives
		}
	}
	
	private void setSubMeasurementValues(PreparedStatementWrapper psWrapper, String openDataUuid, FullSubMeasurement subMeasurement) throws SQLException {
		psWrapper.setAsUuid(1, openDataUuid);

		psWrapper.setLong(2, subMeasurement::getRelativeStartTimeNs);
		psWrapper.setLong(3, subMeasurement::getRelativeEndTimeNs);
		
		psWrapper.setJodaLocalDateTime(4, subMeasurement.getStartTime());
		psWrapper.setJodaLocalDateTime(5, subMeasurement.getEndTime());
		
		psWrapper.setLong(6, subMeasurement::getDurationNs);

		psWrapper.setString(7, () -> { return subMeasurement.getStatus().name(); });
		psWrapper.setString(8, () -> { return subMeasurement.getReason().name(); });
		
		psWrapper.setString(9, subMeasurement::getVersionProtocol);
		psWrapper.setString(10, subMeasurement::getVersionLibrary);
		
		psWrapper.setBoolean(11, subMeasurement.isImplausible());
	}
}
