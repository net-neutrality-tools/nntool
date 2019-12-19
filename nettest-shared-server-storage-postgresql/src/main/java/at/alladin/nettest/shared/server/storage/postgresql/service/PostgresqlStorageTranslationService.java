package at.alladin.nettest.shared.server.storage.postgresql.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.server.service.storage.v1.StorageTranslationService;
import at.alladin.nettest.shared.server.storage.postgresql.helper.PostgresqlMessageSource;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class PostgresqlStorageTranslationService implements StorageTranslationService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public MessageSource getDefaultMessageSource() {
		return new PostgresqlMessageSource(jdbcTemplate, objectMapper);
	}
}
