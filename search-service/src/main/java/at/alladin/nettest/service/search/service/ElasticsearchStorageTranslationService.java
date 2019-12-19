package at.alladin.nettest.service.search.service;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.search.helper.ElasticsearchMessageSource;
import at.alladin.nettest.shared.server.service.storage.v1.StorageTranslationService;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class ElasticsearchStorageTranslationService implements StorageTranslationService {

	@Autowired
	private RestHighLevelClient elasticsearchClient;
	
	@Override
	public MessageSource getDefaultMessageSource() {
		return new ElasticsearchMessageSource(elasticsearchClient);
	}
}
