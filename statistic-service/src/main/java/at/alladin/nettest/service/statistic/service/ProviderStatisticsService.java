package at.alladin.nettest.service.statistic.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.statistic.web.api.v1.dto.ProviderStatisticDto;
import at.alladin.nettest.service.statistic.web.api.v1.dto.ProviderStatisticsRequestParams;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
@Service
public class ProviderStatisticsService {

	private static final Logger logger = LoggerFactory.getLogger(ProviderStatisticsService.class);
	
	private final static String WHERE_TIME = " AND m.start_time > (NOW() - cast(? as interval)) ";
	
	private final static String QUERY_WIFI = 
		"select " + 
		"count(*), " + 
		"percentile_cont(0.5) within group (order by im.rtt_median_ns asc) as ping, " + 
		"percentile_cont(0.5) within group (order by im.throughput_avg_download_bps asc) as down, " + 
		"percentile_cont(0.5) within group (order by im.throughput_avg_upload_bps asc) as up, " + 
		"percentile_cont(0.5) within group (order by m.wifi_network_rssi_dbm asc) as signal_wifi, " + 
		"coalesce(provider_name, provider_shortname, provider_public_ip_as_name) as provider_name, " + 
		"provider_public_ip_asn as provider_asn, " + 
		"provider_country_asn as provider_country " + 
		"from measurements m " + 
		"left join ias_measurements im on im.measurement_open_data_uuid = m.open_data_uuid " + 
		"where throughput_avg_upload_bps is not null and throughput_avg_download_bps is not null and rtt_median_ns is not null " + 
		"and m.initial_network_type_id = 99 %%WHERE%% " + 
		"group by 6,7,8 " +
		"offset ? limit ?";
	
	private final static String QUERY_WIFI_COUNT =
		"SELECT count(*) FROM (" +
			"select " + 
			"coalesce(provider_name, provider_shortname, provider_public_ip_as_name) as provider_name, " + 
			"provider_public_ip_asn as provider_asn, " + 
			"provider_country_asn as provider_country " + 
			"from measurements m " + 
			"left join ias_measurements im on im.measurement_open_data_uuid = m.open_data_uuid " + 
			"where throughput_avg_upload_bps is not null and throughput_avg_download_bps is not null and rtt_median_ns is not null " + 
			"and m.initial_network_type_id = 99 %%WHERE%% " +
			"group by 1,2,3 " +
		") AS count_query"; 
	
	private final static String QUERY_BROWSER = 
		"select " + 
		"count(*), " + 
		"percentile_cont(0.5) within group (order by im.rtt_median_ns asc) as ping, " + 
		"percentile_cont(0.5) within group (order by im.throughput_avg_download_bps asc) as down, " + 
		"percentile_cont(0.5) within group (order by im.throughput_avg_upload_bps asc) as up, " + 
		"coalesce(provider_name, provider_shortname, provider_public_ip_as_name) as provider_name, " + 
		"provider_public_ip_asn as provider_asn, " + 
		"provider_country_asn as provider_country, " + 
		"null::int as signal " + 
		"from measurements m " + 
		"left join ias_measurements im on im.measurement_open_data_uuid = m.open_data_uuid " + 
		"where throughput_avg_upload_bps is not null and throughput_avg_download_bps is not null and rtt_median_ns is not null " + 
		"and agent_type = 'BROWSER' %%WHERE%% " + 
		"group by 5,6,7 " +
		"offset ? limit ?";

	private final static String QUERY_BROWSER_COUNT =
		"SELECT count(*) FROM (" +
			"select " + 
			"coalesce(provider_name, provider_shortname, provider_public_ip_as_name) as provider_name, " + 
			"provider_public_ip_asn as provider_asn, " + 
			"provider_country_asn as provider_country " + 
			"from measurements m  " + 
			"left join ias_measurements im on im.measurement_open_data_uuid = m.open_data_uuid " + 
			"where throughput_avg_upload_bps is not null and throughput_avg_download_bps is not null and rtt_median_ns is not null " + 
			"and agent_type = 'BROWSER' %%WHERE%% " +
			"group by 1,2,3 " +
		") AS count_query"; 

	private final static String QUERY_MOBILE = 
		"select " + 
		"count(*), " + 
		"percentile_cont(0.5) within group (order by im.rtt_median_ns asc) as ping, " + 
		"percentile_cont(0.5) within group (order by im.throughput_avg_download_bps asc) as down, " + 
		"percentile_cont(0.5) within group (order by im.throughput_avg_upload_bps asc) as up, " + 
		"coalesce(percentile_cont(0.5) within group (order by m.mobile_network_lte_rsrp_dbm asc), percentile_cont(0.5) within group (order by m.mobile_network_signal_strength_2g3g_dbm asc)) as signal, " + 
		"coalesce(provider_name, provider_shortname, mobile_sim_operator_name, provider_public_ip_as_name) as provider_name, " + 
		"provider_public_ip_asn as provider_asn, " + 
		"mobile_sim_country_code as provider_country, " +
		"(mobile_sim_operator_mcc || '-' || mobile_sim_operator_mnc) as provider_mnc " +
		"from measurements m " + 
		"left join ias_measurements im on im.measurement_open_data_uuid = m.open_data_uuid " + 
		"where throughput_avg_upload_bps is not null and throughput_avg_download_bps is not null and rtt_median_ns is not null " + 
		"and m.initial_network_type_id not in (97, 98, 99, 106, 107) %%WHERE%% " + 
		"group by 6,9,7,8 " +
		"offset ? limit ?";

	private final static String QUERY_MOBILE_COUNT =
		"SELECT count(*) FROM (" + 
			"SELECT " + 
			"COALESCE(provider_name, provider_shortname, mobile_sim_operator_name, provider_public_ip_as_name) AS provider_name, " + 
			"(mobile_sim_operator_mcc || '-' || mobile_sim_operator_mnc) AS provider_mnc, " + 
			"mobile_sim_country_code AS provider_country " +
			"FROM measurements m  " + 
			"LEFT JOIN ias_measurements im on im.measurement_open_data_uuid = m.open_data_uuid " + 
			"WHERE throughput_avg_upload_bps IS NOT NULL AND throughput_avg_download_bps IS NOT NULL AND rtt_median_ns IS NOT NULL " + 
			"AND m.initial_network_type_id NOT IN (97, 98, 99, 106, 107) %%WHERE%% " +
			"GROUP BY 1,2,3 " +
		") AS count_query";

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
    public Page<ProviderStatisticDto> getProviderStatistics(
    		final ProviderStatisticsRequestParams params, final Pageable pageable) {

    	final StringBuilder where = new StringBuilder("");
    	
    	//default query:
    	String query = QUERY_MOBILE;
    	String queryCount = QUERY_MOBILE_COUNT;
    	boolean isMobile = true;
    	
    	if (params != null) {
    		if (params.getMeasurementType() != null) {
    			switch (params.getMeasurementType()) {
				case BROWSER:
					query = QUERY_BROWSER;
					queryCount = QUERY_BROWSER_COUNT;
					isMobile = false;
					break;
				case MOBILE:
					query = QUERY_MOBILE;
					queryCount = QUERY_MOBILE_COUNT;
					break;
				case WIFI:
					query = QUERY_WIFI;
					queryCount = QUERY_WIFI_COUNT;
					isMobile = false;
					break;
				}
    		}
    		
    		if (params.getPeriod() != null) {
    			where.append(WHERE_TIME);
    		}
    	}

    	final boolean isMobileFinal = isMobile;
    	
    	logger.debug("{}", queryCount.replace("%%WHERE%%", where.toString()));
    	final Long count = jdbcTemplate.query(queryCount.replace("%%WHERE%%", where.toString()),
			new PreparedStatementSetterImpl(params, null), 
			new ResultSetExtractor<Long>() {
				@Override
				public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
					if (rs.next()) {
						return rs.getLong(1);
					}
					
					return 0L;
				}
		});
    	logger.debug("count: {}", count);
    	
    	logger.debug("{}", query.replace("%%WHERE%%", where.toString()));
    	final List<ProviderStatisticDto> result = jdbcTemplate.query(query.replace("%%WHERE%%", where.toString()), 
			new PreparedStatementSetterImpl(params, pageable),
			new RowMapper<ProviderStatisticDto>() {
				@Override
				public ProviderStatisticDto mapRow(ResultSet rs, int rowNum) throws SQLException {
					int i = 1;
					
					final ProviderStatisticDto dto = new ProviderStatisticDto();
					dto.setCount(rs.getLong(i++));
					dto.setRttNs((long) rs.getDouble(i++));
					dto.setDownloadBps((long) rs.getDouble(i++));
					dto.setUploadBps((long) rs.getDouble(i++));
					dto.setSignalStrengthDbm((int) rs.getDouble(i++));
					dto.setProviderName(rs.getString(i++));
					dto.setProviderAsn(rs.getInt(i++));
					dto.setCountryCode(rs.getString(i++));
					
					if (isMobileFinal) {
						dto.setMccMnc(rs.getString(i++));
					}
					
					return dto;
				}
		});
    	
    	return new PageImpl<ProviderStatisticDto>(result, pageable, count);
    }
    
    public static class PreparedStatementSetterImpl implements PreparedStatementSetter {
		
    	final ProviderStatisticsRequestParams params;
    	final Pageable pageable;

    	public PreparedStatementSetterImpl(final ProviderStatisticsRequestParams params, final Pageable pageable) {
			this.params = params;
			this.pageable = pageable;
		}
    	
		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			int i = 1;
			if (params != null) {
				if (params.getPeriod() != null) {
					ps.setString(i++, params.getPeriod() +  " minutes");
				}
			}
			
			if (pageable != null) {
				ps.setLong(i++, pageable.getOffset());
				ps.setInt(i++, pageable.getPageSize());
			}
		}
	}
}
