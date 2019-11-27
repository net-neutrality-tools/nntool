package at.alladin.nettest.shared.server.opendata.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;
import at.alladin.nettest.shared.server.config.ElasticSearchProperties;
import at.alladin.nettest.shared.server.opendata.jdbc.IasMeasurementPreparedStatementSetter;
import at.alladin.nettest.shared.server.opendata.jdbc.MeasurementPreparedStatementSetter;
import at.alladin.nettest.shared.server.opendata.jdbc.QoSMeasurementPreparedStatementSetter;
import at.alladin.nettest.shared.server.opendata.jdbc.bulk.IasMeasurementBatchPreparedStatementSetter;
import at.alladin.nettest.shared.server.opendata.jdbc.bulk.MeasurementBatchPreparedStatementSetter;
import at.alladin.nettest.shared.server.opendata.jdbc.bulk.QoSMeasurementBatchPreparedStatementSetter;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class OpenDataMeasurementService {

	private static final Logger logger = LoggerFactory.getLogger(OpenDataMeasurementService.class);
	
	private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
		    .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
		    .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
		    .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
		    .optionalStart().appendOffset("+HH", "Z").optionalEnd()
		    .toFormatter();
	
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
		+ ") ON CONFLICT DO NOTHING";

	
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
		+ ") ON CONFLICT DO NOTHING";
	
	private static final String INSERT_QOS_MEASUREMENT_SQL = "INSERT INTO qos_measurements ("
		+ "measurement_open_data_uuid, relative_start_time_ns, relative_end_time_ns, start_time, end_time, duration_ns, status, reason, version_protocol, version_library, implausible"
		+ ") VALUES ("
		+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
		+ "?"
		+ ") ON CONFLICT DO NOTHING";
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired(required = false)
	@Qualifier("elasticSearchClient")
	private RestHighLevelClient elasticSearchClient;
	
	@Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
	
	@Autowired(required = false)
	private ElasticSearchProperties elasticsearchProperties;
	
	public boolean areOpenDataDatabasesAvailable() {
		return elasticSearchClient != null || jdbcTemplate != null;
	}
	
	public void storeOpenDataMeasurement(FullMeasurementResponse measurementDto) {
		final String openDataUuid = measurementDto.getOpenDataUuid();
		
		if (elasticSearchClient != null) {
			try {
				storeMeasurementInElasticsearch(measurementDto);
				logger.info("Saved result (open_data_uuid: {}) in Elasticsearch", openDataUuid);
			} catch (Exception ex) {
				logger.error("Could not save result (open_data_uuid: {}) in Elasticsearch", openDataUuid, ex);
			}
		}
		
		// Store measurement result in PostgreSQL if available
		if (jdbcTemplate != null) {
			try {
				storeMeasurementInPostgresql(measurementDto);
				logger.info("Saved result (open_data_uuid: {}) in PostgreSQL", openDataUuid);
			} catch (Exception ex) {
				logger.error("Could not save result (open_data_uuid: {}) in PostgreSQL", openDataUuid, ex);
			}
		}
	}
	
	public void storeMeasurementInElasticsearch(FullMeasurementResponse measurementDto) throws Exception {
		@SuppressWarnings("unchecked")
		final IndexRequest indexRequest = new IndexRequest(elasticsearchProperties.getIndex())
				.id(measurementDto.getOpenDataUuid())
				.source(objectMapper.convertValue(measurementDto, Map.class));
		
		try {
			final IndexResponse indexResponse = elasticSearchClient.index(indexRequest, RequestOptions.DEFAULT);
			
			logger.debug("IndexRequest response: {}", indexResponse);
		} catch (IOException ex) {
			throw ex;
		}
	}
	
	public void storeMeasurementInPostgresql(FullMeasurementResponse measurementDto) {
		final String openDataUuid = measurementDto.getOpenDataUuid();
		
		jdbcTemplate.update(INSERT_MEASUREMENT_SQL, new MeasurementPreparedStatementSetter(measurementDto));
		
		Map<MeasurementTypeDto, FullSubMeasurement> subMeasurements = measurementDto.getMeasurements();
		
		FullSubMeasurement iasSubMeasurement = subMeasurements.get(MeasurementTypeDto.SPEED);
		if (iasSubMeasurement instanceof FullSpeedMeasurement) {
			jdbcTemplate.update(INSERT_IAS_MEASUREMENT_SQL, new IasMeasurementPreparedStatementSetter((FullSpeedMeasurement) iasSubMeasurement, openDataUuid));
		}
		
		FullSubMeasurement qosSubMeasurement = subMeasurements.get(MeasurementTypeDto.QOS);
		if (qosSubMeasurement != null && qosSubMeasurement instanceof FullQoSMeasurement) {
			jdbcTemplate.update(INSERT_QOS_MEASUREMENT_SQL, new QoSMeasurementPreparedStatementSetter((FullQoSMeasurement) qosSubMeasurement, openDataUuid));
		}
	}
	
	////
	
	public void bulkStoreOpenDataMeasurement(List<Map<String, Object>> openDataMeasurements) {
		if (elasticSearchClient != null) {
			try {
				bulkStoreMeasurementInElasticsearch(openDataMeasurements);
				logger.info("Saved {} bulk open-data measurements in Elasticsearch", openDataMeasurements.size());
			} catch (Exception ex) {
				logger.info("Could not bulk save {} open-data measurements in Elasticsearch", openDataMeasurements.size(), ex);
			}
		}
		
		if (jdbcTemplate != null) {
			try {
				bulkStoreMeasurementInPostgresql(openDataMeasurements);
				logger.info("Saved {} bulk open-data measurements in PostgreSQL", openDataMeasurements.size());
			} catch (Exception ex) {
				logger.info("Could not bulk save {} open-data measurements in PostgreSQL", openDataMeasurements.size(), ex);
			}
			
		}
	}
	
	public void bulkStoreMeasurementInElasticsearch(List<Map<String, Object>> measurements) throws Exception {
		if (measurements == null || measurements.isEmpty()) {
			return;
		}
		
		final BulkRequest bulkRequest = new BulkRequest();
		
		measurements.forEach(m -> {
			bulkRequest.add(
				new IndexRequest(elasticsearchProperties.getIndex())
					.id(String.valueOf(m.get("open_data_uuid")))
					.source(m)
			);
		});
		
		elasticSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
	}
	
	public void bulkStoreMeasurementInPostgresql(List<Map<String, Object>> measurements) {
		final List<FullMeasurementResponse> mappedMeasurements = measurements.stream().map(m -> {
			return objectMapper.convertValue(m, FullMeasurementResponse.class);
		}).collect(Collectors.toList());
		
		// insert measurement
		jdbcTemplate.batchUpdate(INSERT_MEASUREMENT_SQL, new MeasurementBatchPreparedStatementSetter(mappedMeasurements));
		
		// insert IAS measurement
		jdbcTemplate.batchUpdate(INSERT_IAS_MEASUREMENT_SQL, new IasMeasurementBatchPreparedStatementSetter(mappedMeasurements));
		
		// insert QOS measurement
		jdbcTemplate.batchUpdate(INSERT_QOS_MEASUREMENT_SQL, new QoSMeasurementBatchPreparedStatementSetter(mappedMeasurements));
	}
	
	public String getLatestStartTime() { 
		final String latestStartTimeElasticsearch = getLatestStartTimeFromElasticsearch();
		final String latestStartTimePostgresql = getLatestStartTimeFromPostgresql();
		
		if (StringUtils.isEmpty(latestStartTimeElasticsearch)) {
			return latestStartTimePostgresql;
		} else if (StringUtils.isEmpty(latestStartTimePostgresql)) {
			return latestStartTimeElasticsearch;
		}
		
		final LocalDateTime localDateTimeElasticsearch = LocalDateTime.parse(latestStartTimeElasticsearch, ISO_LOCAL_DATE_TIME_FORMATTER);
		final LocalDateTime localDateTimePostgresql = LocalDateTime.parse(latestStartTimePostgresql, ISO_LOCAL_DATE_TIME_FORMATTER);
		
		if (localDateTimeElasticsearch.isBefore(localDateTimePostgresql)) {
			return ISO_LOCAL_DATE_TIME_FORMATTER.format(localDateTimeElasticsearch);
		} else {
			return ISO_LOCAL_DATE_TIME_FORMATTER.format(localDateTimePostgresql);
		}
	}
	
	public String getLatestStartTimeFromElasticsearch() {
		try {
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			
			searchSourceBuilder.aggregation(
				AggregationBuilders.max("max_start_time").field("start_time").valueType(ValueType.DATE)
			);
			
			searchSourceBuilder.size(0); // Return only aggregation results.
			
			final SearchRequest searchRequest = new SearchRequest();
			searchRequest.indices(elasticsearchProperties.getIndex());
			searchRequest.source(searchSourceBuilder);
			
			final SearchResponse searchResponse = elasticSearchClient.search(searchRequest, RequestOptions.DEFAULT);
			
			final Aggregation agg = searchResponse.getAggregations().get("max_start_time");
			
			if (agg != null && agg instanceof ParsedMax) {
				final ParsedMax max = (ParsedMax) agg;
				final String value = max.getValueAsString();
				
				// Aggregation returns -Infinity if no data is available.
				if (!"-Infinity".equals(value)) {
					return value;
				}
			}
		} catch (Exception ex) {
			logger.error("Could not get latest start_time value.", ex);
		}
		
		return null;
	}
	
	public String getLatestStartTimeFromPostgresql() {
		return jdbcTemplate.query("SELECT to_char(max(start_time), 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') FROM measurements", new ResultSetExtractor<String>() {

			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				
				return rs.getString(1);
			}
		});
	}
}
