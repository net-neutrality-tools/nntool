package at.alladin.nettest.service.search.helper;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.alladin.nettest.shared.server.helper.spring.CachingAndReloadingMessageSource;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class ElasticsearchMessageSource extends CachingAndReloadingMessageSource<Map<String, Object>> {

	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchMessageSource.class);
	
	private static final String TRANSLATION_INDEX = "nntool_translation";
	
	private final RestHighLevelClient elasticsearchClient;
	
	public ElasticsearchMessageSource(RestHighLevelClient elasticsearchClient) {
		this.elasticsearchClient = elasticsearchClient;
	}
	
	////
	
	protected String getTranslation(String code, Map<String, Object> translationObject) {
		return String.valueOf(translationObject.get(code));
	}
	
	protected Map<String, Object> loadTranslation(final Locale locale) {
		logger.info("Loading translation for locale {} from Elasticsearch.", locale);
		
		final GetRequest getRequest = new GetRequest(TRANSLATION_INDEX, locale.getLanguage());
		
		try {
			final GetResponse getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);
			
			return getResponse.getSourceAsMap();
		} catch (IOException e) {
			logger.error("Could not load translation form Elasticsearch.", e);
			return null;
		}
	}
}
