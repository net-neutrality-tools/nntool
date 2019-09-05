package at.alladin.nettest.service.map.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
//import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import at.alladin.nettest.shared.helper.GsonBasicHelper;
//import at.alladin.nettest.shared.server.helper.GsonHelper;
import at.alladin.nettest.shared.server.model.ServerSettings;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
//@ConditionalOnBean(name = "jdbcTemplate") // TODO: check if SQL database is present
public class SqlSettingsService {

	private final Logger logger = LoggerFactory.getLogger(SqlSettingsService.class);
	
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
