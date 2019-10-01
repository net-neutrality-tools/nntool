package at.alladin.nettest.service.search.service;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import at.alladin.nettest.service.search.config.SearchServiceProperties;
import at.alladin.nettest.service.search.exception.BadExportRequestException;
import at.alladin.nettest.service.search.exception.SearchException;

/**
 * This service is responsible for search requests.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class SearchService {

	private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
	
	private static final TimeValue DEFAULT_TIMEOUT = new TimeValue(5, TimeUnit.SECONDS);
	
	@Autowired
	private SearchServiceProperties searchServiceProperties;
	
	@Autowired
	private RestHighLevelClient elasticsearchClient;	
	
	public Page<Map<String, Object>> findAll(String queryString, Pageable pageable) {
		final int searchMaxPageSize = searchServiceProperties.getSearch().getMaxPageSize();
		
		return findAll(queryString, pageable, searchMaxPageSize);
	}
	
	public Page<Map<String, Object>> findAll(String queryString, Pageable pageable, int maxPageSize) {
		if (pageable.getPageSize() > maxPageSize) {
			throw new BadExportRequestException("Only " + maxPageSize + " items can be queried at once."); 
		}
		
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		final QueryBuilder queryBuilder;
		
		if (StringUtils.hasLength(queryString)) {
			String customQueryString = new String(queryString);
			
			// TODO: restrict queryStringQuery
			
			/*queryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.queryStringQuery(customQueryString));*/
			//queryBuilder = QueryBuilders.queryStringQuery(customQueryString)/*.allowLeadingWildcard(false)*/;
			
			// TODO ...
			if (!customQueryString.contains(":")) {
				if (!customQueryString.startsWith("*")) {
					customQueryString = "*" + customQueryString;
				}
				
				if (!customQueryString.endsWith("*")) {
					customQueryString = customQueryString + "*";
				}
			}
			
			queryBuilder = QueryBuilders.queryStringQuery(customQueryString).analyzeWildcard(true);
		} else {
			queryBuilder = QueryBuilders.matchAllQuery();
		}
		
		searchSourceBuilder.query(queryBuilder);
		
		// pageable
		searchSourceBuilder.from((int)pageable.getOffset());
		searchSourceBuilder.size(pageable.getPageSize());
		
		// sort
		// pageable sort: localhost:8083/api/v1/measurements?q=*&sort=start_time,desc&sort=end_time,desc
		// elasticsearch sort: localhost:8083/api/v1/measurements?q=*&sort=start_time:desc&sort=end_time:desc
		// We currently only support pageable sort method with ,desc
		
		pageable.getSort().forEach(s -> {
			searchSourceBuilder.sort(
				//SortBuilders.fieldSort(s.getProperty()).order(s.getDirection() == Direction.ASC ? SortOrder.ASC : SortOrder.DESC)
				SortBuilders.fieldSort(s.getProperty()).order(SortOrder.fromString(s.getDirection().toString()))
			);
		});
		
		// Execute Query
		final SearchResult result = executeSearchQuery(searchSourceBuilder);
		
		return new PageImpl<>(result.getResults(), pageable, result.getTotalHits());
	}

	public List<Map<String, Object>> findByDateRange(Integer year, Integer month, Integer day) {
		String gte = year + "-" + String.format("%02d", month);
		String lte = year + "-" + String.format("%02d", month);
		
		if (day != null) {
			gte += "-" + String.format("%02d", day);
			lte += "-" + String.format("%02d", day);
		} else {
			gte += "-01";
			lte += "-" + YearMonth.of(year, month).lengthOfMonth();
		}
		
		final QueryBuilder queryBuilder = QueryBuilders
				.rangeQuery("start_time")
				.gte(gte)
				.lte(lte);
		
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		searchSourceBuilder.query(queryBuilder);
		
		// Execute Query
		return executeSearchQuery(searchSourceBuilder).getResults();
	}
	
	public Map<String, Object> findOneByOpenDataUuid(String openDataUuid) {
		final String index = searchServiceProperties.getElasticsearch().getIndex();
		final GetRequest getRequest = new GetRequest(index, openDataUuid);
		
		try {
			final GetResponse getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);
			
			return getResponse.getSourceAsMap();
		} catch (IOException e) {
			throw new SearchException(e);
		}
	}
	
	////
	
	private SearchResult executeSearchQuery(SearchSourceBuilder searchSourceBuilder) {
		// Add timeout if none is set
		if (searchSourceBuilder.timeout() == null) {
		
			TimeValue timeoutTimeValue;
			try {
				// parseTimeValue throws an exception if the String is malformed.
				final String queryTimeoutString = searchServiceProperties.getElasticsearch().getQueryTimeout();
				timeoutTimeValue = TimeValue.parseTimeValue(queryTimeoutString, DEFAULT_TIMEOUT, "timeout");
			} catch (IllegalArgumentException ex) {
				timeoutTimeValue = DEFAULT_TIMEOUT;
			}
	
			searchSourceBuilder.timeout(timeoutTimeValue);
		}
		
		final String index = searchServiceProperties.getElasticsearch().getIndex();
		
		final SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(index);
		searchRequest.source(searchSourceBuilder);
		
		try {
			final SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
			
			final SearchHits searchHits = searchResponse.getHits();
			
			List<Map<String, Object>> mapList = Stream.of(searchHits.getHits())
				.map(SearchHit::getSourceAsMap)
				.collect(Collectors.toList());
			
			final SearchResult result = new SearchResult(mapList, searchHits.getTotalHits().value);
			
			logger.debug("Query: {}, result: {}", searchSourceBuilder.query().toString(), result);
			
			return result;
		} catch (IOException e) {
			throw new SearchException(e);
		}
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	private class SearchResult {
		
		final List<Map<String, Object>> results;
		final long totalHits;
		
		SearchResult(List<Map<String, Object>> results, long totalHits) {
			this.results = results;
			this.totalHits = totalHits;
		}
		
		public List<Map<String, Object>> getResults() {
			return results;
		}
		
		public long getTotalHits() {
			return totalHits;
		}

		@Override
		public String toString() {
			return "SearchResult [results.size=" + results.size() + ", totalHits=" + totalHits + "]";
		}
	}
}
