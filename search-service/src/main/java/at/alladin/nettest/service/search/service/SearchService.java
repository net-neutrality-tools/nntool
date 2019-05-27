package at.alladin.nettest.service.search.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.search.exception.SearchException;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class SearchService {

	@Autowired
	private RestHighLevelClient elasticsearchClient;
	
	public Page</*BriefMeasurementResponse*/Map<String, Object>> findAll(Pageable pageable) throws SearchException {
		
		final SearchRequest searchRequest = new SearchRequest();
		
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		//searchSourceBuilder.query(QueryBuilders.typeQuery("measurement"));
		searchSourceBuilder.query(QueryBuilders.existsQuery("open_data_uuid"));
		
		// pageable
		searchSourceBuilder.from((int)pageable.getOffset());
		searchSourceBuilder.size(pageable.getPageSize());
		
		// TODO: sort
		//pageable.getSort().
		//searchSourceBuilder.sort
		
		searchSourceBuilder.trackScores(false);
		searchSourceBuilder.explain(false);
		
		searchRequest.source(searchSourceBuilder);
		
		try {
			final SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
			
			final SearchHits searchHits = searchResponse.getHits();
			
			List<Map<String, Object>> strList = Stream.of(searchHits.getHits())
				.map(h -> {
					return h.getSourceAsMap();
				})
				.collect(Collectors.toList());
			
			return new PageImpl<>(strList, pageable, searchHits.getTotalHits().value);
		} catch (IOException e) {
			throw new SearchException(e);
		}
	}
}
