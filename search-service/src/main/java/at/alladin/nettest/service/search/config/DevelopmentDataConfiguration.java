package at.alladin.nettest.service.search.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.DeviceInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.OperatingSystemInfoDto;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
@Profile("dev")
public class DevelopmentDataConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(DevelopmentDataConfiguration.class);
	
	private static final int NUM_DOCS = 200;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private SearchServiceProperties searchServiceProperties;
	
	@Autowired
	private RestHighLevelClient elasticsearchClient;
	
	@PostConstruct
	private void postConstruct() throws IOException {
		final String index = searchServiceProperties.getElasticsearch().getIndex();
		
		// index
		
		final boolean indexExists = elasticsearchClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
		
		if (indexExists) {
			logger.debug("{} index already created", index);
		} else {
			logger.debug("Creating {} index", index);

			final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
			elasticsearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
		}
		
		// documents

		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.existsQuery("open_data_uuid"));
		
		final SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(index);
		searchRequest.source(searchSourceBuilder);
		
		final SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
		
		if (searchResponse.getHits().getTotalHits().value > 0) {
			logger.debug("Elasticsearch demo data already inserted");
			return;
		}
		
		logger.debug("Inserting Elasticsearch demo data");
		
		final BulkRequest bulkRequest = new BulkRequest();
		
		for (int i = 0; i < NUM_DOCS; i++) {
			final FullMeasurementResponse measurement = generateRandomFullMeasurementResponse();
			
			bulkRequest.add(
				new IndexRequest(index).id(measurement.getOpenDataUuid())
					.source(objectMapper.writeValueAsBytes(measurement), XContentType.JSON)
			);
		}
		
		final BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		logger.debug("Indexing {} documents took {}", NUM_DOCS, bulkResponse.getTook());
	}
	
	private FullMeasurementResponse generateRandomFullMeasurementResponse() {
		final FullMeasurementResponse fullMeasurementResponse = new FullMeasurementResponse();
		
		fullMeasurementResponse.setOpenDataUuid(UUID.randomUUID().toString());
		
		fullMeasurementResponse.setStartTime(LocalDateTime.now().minusHours(1));
		fullMeasurementResponse.setEndTime(LocalDateTime.now().minusHours(1).plusSeconds(30));
		fullMeasurementResponse.setDurationNs(30 * 1000000000l);
		fullMeasurementResponse.setSource("nntool_dev_source");
		
		//
		
		final MeasurementAgentInfoDto agentInfo = new MeasurementAgentInfoDto();
		
		agentInfo.setLanguage(randomValueOf("en_US", "de-AT", "de-DE"));
		agentInfo.setTimezone(randomValueOf("Europe/Vienna", "Europe/Berlin", "Europe/Amsterdam"));
		agentInfo.setType(randomValueOf(MeasurementAgentTypeDto.MOBILE, MeasurementAgentTypeDto.DESKTOP, MeasurementAgentTypeDto.BROWSER));
		agentInfo.setAppVersionName(randomValueOf("1.0.0", "0.0.1", "0.1.0"));
		agentInfo.setAppVersionCode(randomValueOf(42, 43, 44, 45));
		agentInfo.setAppGitRevision(randomValueOf("cba56293", "cbc562e3", "aca5d243", "ffa5a2f3"));
		
		fullMeasurementResponse.setAgentInfo(agentInfo);
		
		//
		
		final DeviceInfoDto deviceInfo = new DeviceInfoDto();
		
		deviceInfo.setCodeName(randomValueOf("bullhead", "iphone7,2"));
		deviceInfo.setFullName(randomValueOf("Motorola something", "Iphone 6 Plus"));
		deviceInfo.setModel(randomValueOf("MOTOG3", "something"));
		
		final OperatingSystemInfoDto osInfo = new OperatingSystemInfoDto();
		
		osInfo.setName(randomValueOf("Android", "iOS", "Linux", "Windows"));
		osInfo.setVersion(randomValueOf("8.1", "7.1", "6.5"));
		osInfo.setApiLevel(randomValueOf("27", "23", "28"));
		osInfo.setCpuMin(0.1);
		osInfo.setCpuMax(0.4);
		osInfo.setCpuAverage(0.2);
		osInfo.setCpuMedian(0.3);
		osInfo.setMemoryMin(0.1);
		osInfo.setMemoryMax(0.4);
		osInfo.setMemoryAverage(0.2);
		osInfo.setMemoryMedian(0.3);
		
		deviceInfo.setOsInfo(osInfo);
		
		fullMeasurementResponse.setDeviceInfo(deviceInfo);
		
		//
		
		final List<GeoLocationDto> geoLocations = new ArrayList<>();
		
		GeoLocationDto loc = new GeoLocationDto();
		
        loc.setTime(new LocalDateTime(2011, 2, 6, 22, 42, 14));
        loc.setAccuracy(2.1);
        loc.setAltitude(184.3);
        loc.setHeading(0.0);
        loc.setSpeed(0.0);
        loc.setProvider("accurate location provider");
        loc.setLatitude(randomValueOf(32.747895, 13.55, 15.74, 19.32));
        loc.setLongitude(randomValueOf(-97.092505, -60.34, 23.63, 90.34));
        loc.setRelativeTimeNs((long) (1.4e10 + 10));
        
        geoLocations.add(loc);
        
        loc = new GeoLocationDto();
        loc.setTime(new LocalDateTime(2011, 2, 6, 22, 42, 38));
        loc.setAccuracy(12.1);
        loc.setAltitude(184.7);
        loc.setHeading(0.0);
        loc.setSpeed(0.0);
        loc.setProvider("accurate location provider");
        loc.setLatitude(randomValueOf(32.7476, 13.58, 15.79, 19.36));
        loc.setLongitude(randomValueOf(-97.094, -60.32, 23.68, 90.38));
        loc.setRelativeTimeNs((long) (3.8e10 + 10));
        
        geoLocations.add(loc);
		
		fullMeasurementResponse.setGeoLocations(geoLocations);
		
		//
		
		final Map<MeasurementTypeDto, FullSubMeasurement> measurements = new HashMap<>();
		
		fullMeasurementResponse.setMeasurements(measurements);
		
		//
		
		final NetworkInfoDto networkInfo = new NetworkInfoDto();
		
		fullMeasurementResponse.setNetworkInfo(networkInfo);
		
		//
		
		return fullMeasurementResponse;
	}
	
	private <T> T randomValueOf(T... values) {
		if (values == null || values.length < 1) {
			return null;
		}
		
		int randomIndex = (int)(Math.random() * values.length);
		
		return values[randomIndex]; 
	}
}
