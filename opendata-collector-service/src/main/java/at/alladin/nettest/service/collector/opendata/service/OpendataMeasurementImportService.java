package at.alladin.nettest.service.collector.opendata.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.collector.opendata.config.OpendataCollectorServiceProperties;
import at.alladin.nettest.shared.server.config.ElasticSearchProperties;

/**
 *
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@Service
@ConditionalOnProperty(name = "opendata-collector.opendata-import.enabled")
public class OpendataMeasurementImportService {

	private static final Logger logger = LoggerFactory.getLogger(OpendataMeasurementImportService.class);

	@Autowired
	private OpendataCollectorServiceProperties opendataCollectorServiceProperties;

	@Autowired(required = false)
	@Qualifier("elasticSearchClient")
	private RestHighLevelClient elasticSearchClient;
	
	@Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
	
	public void insertMeasurements(List<Map<String, Object>> measurements) throws IOException {
		
		if (elasticSearchClient != null) {
			final ElasticSearchProperties elasticSearchProperties = opendataCollectorServiceProperties.getElasticsearch();
			
			bulkInsertIntoElasticsearch(measurements, elasticSearchProperties.getIndex());		
		}
		
		if (jdbcTemplate != null) {
			// TODO: insert into postgresql
		}
	}
	
	private void bulkInsertIntoElasticsearch(List<Map<String, Object>> measurements, String index) throws IOException {
		if (measurements == null || measurements.isEmpty()) {
			return;
		}
		
		final BulkRequest bulkRequest = new BulkRequest();
		
		measurements.forEach(m -> {
			bulkRequest.add(
				new IndexRequest(index)
					.id(String.valueOf(m.get("open_data_uuid")))
					.source(m)
			);
		});
		
		elasticSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
	}

	public String getLatestStartTime() { // TODO: min(max(postgresql), max(elasticsearch))
		try {
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			
			searchSourceBuilder.aggregation(
				AggregationBuilders.max("max_start_time").field("start_time").valueType(ValueType.DATE)
			);
			
			searchSourceBuilder.size(0); // Return only aggregation results.
			
			final SearchRequest searchRequest = new SearchRequest();
			searchRequest.indices(/*index*/"nntool_portal");
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
}
