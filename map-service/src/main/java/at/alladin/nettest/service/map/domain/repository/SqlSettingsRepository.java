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

package at.alladin.nettest.service.map.domain.repository;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.google.gson.Gson;

import at.alladin.nettest.shared.helper.GsonBasicHelper;
import at.alladin.nettest.shared.server.model.ServerSettings;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */

@Repository
//@ConditionalOnBean(name = "jdbcTemplate") // TODO: check if SQL database is present
public class SqlSettingsRepository {


		private final Logger logger = LoggerFactory.getLogger(SqlSettingsRepository.class);
		
		/**
		 * 
		 */
		private static final String GET_SETTINGS_SQL = "SELECT json AS json from settings LIMIT 1";
		
		/**
		 * 
		 */
		@Autowired
		private JdbcTemplate jdbcTemplate;
		
		/**
		 * 
		 */
		private ServerSettings cachedSettings;
		
		/**
		 * 
		 */
		//private final Gson gson = GsonHelper.createDatabaseGsonBuilder().create(); // TODO: create bean and inject
		private final Gson gson = GsonBasicHelper.getDateTimeGsonBuilder().create();
		
		/**
		 * 
		 */
		
		private final ResultSetExtractor<ServerSettings> settingsExtractor = new ResultSetExtractor<ServerSettings>() {

			@Override
			public ServerSettings extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					return gson.fromJson(rs.getString("json"), ServerSettings.class);
				}
				
				return null;
			}
		};
		
		
		/**
		 * 
		 */
		@PostConstruct
		private void postConstruct() {
			getSettings();
			
			logger.info("Loaded settings from SQL database: {}", cachedSettings);
			
			// TODO: use Spring caching instead of local variable caching?
			// TODO: re-select settings from database every x seconds
		}
		
		/**
		 * 
		 * @return
		 */
		public ServerSettings getSettings() {
			
			if (cachedSettings != null) {
				return cachedSettings;
			}
			
			try {
				final ServerSettings settings = jdbcTemplate.query(GET_SETTINGS_SQL, settingsExtractor);
				cachedSettings = settings;
			} catch (Exception ex) {
				ex.printStackTrace();
				// TODO
			}
			
			return cachedSettings;
		}

}
