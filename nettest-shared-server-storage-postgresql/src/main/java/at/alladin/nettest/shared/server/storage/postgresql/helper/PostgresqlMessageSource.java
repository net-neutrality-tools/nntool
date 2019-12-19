/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.shared.server.storage.postgresql.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.server.helper.spring.CachingAndReloadingMessageSource;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class PostgresqlMessageSource extends CachingAndReloadingMessageSource<Map<String, String>> {

	private static final Logger logger = LoggerFactory.getLogger(PostgresqlMessageSource.class);
	
	private static final String TRANSLATION_SQL_QUERY = "SELECT json::text FROM translations WHERE language_code = ?";
	
	private final JdbcTemplate jdbcTemplate;
	
	private final ObjectMapper objectMapper;
	
	public PostgresqlMessageSource(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.objectMapper = objectMapper;
	}
	
	////
	
	protected String getTranslation(String code, Map<String, String> translationObject) {
		return translationObject.get(code);
	}
	
	protected Map<String, String> loadTranslation(final Locale locale) {
		try {
			return jdbcTemplate.query(
				TRANSLATION_SQL_QUERY, 
				new Object[] { locale.getLanguage() }, 
				new ResultSetExtractor<Map<String, String>>() {
	
					@Override
					public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
						rs.next();
						
						final String jsonString = rs.getString(1);
						try {
							final Map<String, String> translationObject = objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {});
							return translationObject;
						} catch (Exception e) {
							logger.error("Could not parse translation from PostgreSQL.", e);
						}
						
						return null;
					}
				}
			);
		} catch (Exception ex) {
			// TODO: print
		}
		
		return null;
	}
}
