package at.alladin.nettest.shared.server.storage.couchdb.config;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SubMeasurement;
import at.alladin.nettest.spring.couchdb.config.CouchDbConfiguration;
import at.alladin.nettest.spring.couchdb.config.CouchDbProperties;
import at.alladin.nettest.spring.data.couchdb.repository.config.EnableCouchDbRepositories;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@EnableCouchDbRepositories("at.alladin.nettest.shared.server.storage.couchdb.domain.repository")
@ComponentScan({
	"at.alladin.nettest.shared.server.storage.couchdb",
})
@Configuration
public class CouchDbStorageConfiguration extends CouchDbConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(CouchDbStorageConfiguration.class);
	
	public CouchDbStorageConfiguration(CouchDbProperties couchDbProperties) {
		super(couchDbProperties);
		logger.info("Initialising couchDBStorageConfiguration");
	}
	
	@Primary
	@Bean
	public GsonBuilder couchDbGsonBuilder() {
		final GsonBuilder gsonBuilder = super.couchDbGsonBuilder();
		
		gsonBuilder.registerTypeAdapter(Measurement.class, new MeasurementConverter());
		
		logger.info("registered measurement enabled gsonBuilder");
		
		return gsonBuilder;
	}
	
	public static class MeasurementConverter implements JsonDeserializer<Measurement> {
		
		private static Gson gson = new Gson();
		
		@Override
		public Measurement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			final Measurement ret = gson.fromJson(json, Measurement.class);
			
			final JsonElement measurementsJson = json.getAsJsonObject().get("measurements");
			if (measurementsJson != null && measurementsJson.isJsonObject()) {
				for (Entry<String, JsonElement> entry : measurementsJson.getAsJsonObject().entrySet()) {
					final MeasurementTypeDto type;
					try {
						type = MeasurementTypeDto.valueOf(entry.getKey());
					} catch (IllegalArgumentException ex) {
						ex.printStackTrace();
						continue;
					}
					Class<? extends SubMeasurement> subMeasurementClass = SubMeasurement.class;
					switch (type) {
					case QOS:
						subMeasurementClass = QoSMeasurement.class;
						break;
					case SPEED:
						subMeasurementClass = SpeedMeasurement.class;
						break;
					}
					ret.getMeasurements().put(type, gson.fromJson(entry.getValue(), subMeasurementClass));
				}
			}
			
			return ret;
		}
		
	}
}
