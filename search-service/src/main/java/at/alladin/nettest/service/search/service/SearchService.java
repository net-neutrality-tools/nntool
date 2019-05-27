package at.alladin.nettest.service.search.service;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import at.alladin.nettest.service.search.exception.SearchException;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class SearchService {

	private static final TimeValue DEFAULT_TIMEOUT = new TimeValue(5, TimeUnit.SECONDS);
	
	@Value("${search-service.elasticsearch.index}")
	private String index;
	
	@Value("${search-service.elasticsearch.queryTimeout}")
	private String queryTimeoutString;
	
	@Autowired
	private RestHighLevelClient elasticsearchClient;
	
	public Page<Map<String, Object>> findAll(String queryString, Pageable pageable) throws SearchException {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		final QueryBuilder queryBuilder;
		
		if (StringUtils.hasLength(queryString)) {
			// TODO: restrict queryStringQuery
			
			/*queryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.queryStringQuery(queryString));*/
			queryBuilder = QueryBuilders.queryStringQuery(queryString)/*.allowLeadingWildcard(false)*/;
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
		
		// Timeout
		
		TimeValue timeoutTimeValue;
		
		try {
			// parseTimeValue throws an exception if the String is malformed.
			timeoutTimeValue = TimeValue.parseTimeValue(queryTimeoutString, DEFAULT_TIMEOUT, "timeout");
		} catch (IllegalArgumentException ex) {
			timeoutTimeValue = DEFAULT_TIMEOUT;
		}

		searchSourceBuilder.timeout(timeoutTimeValue);
		
		// Execute Query
		
		final SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(index);
		searchRequest.source(searchSourceBuilder);
		
		try {
			final SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
			
			final SearchHits searchHits = searchResponse.getHits();
			
			List<Map<String, Object>> strList = Stream.of(searchHits.getHits())
				.map(SearchHit::getSourceAsMap)
				.collect(Collectors.toList());
			
			return new PageImpl<>(strList, pageable, searchHits.getTotalHits().value);
		} catch (IOException e) {
			throw new SearchException(e);
		}
	}

	public Map<String, Object> findOneByOpenDataUuid(String openDataUuid) {
		final GetRequest getRequest = new GetRequest(index, openDataUuid);
		
		try {
			final GetResponse getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);
			
			return getResponse.getSourceAsMap();
		} catch (IOException e) {
			throw new SearchException(e);
		}
	}
}
