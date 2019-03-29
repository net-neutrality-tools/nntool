package at.alladin.nettest.couchdb.client.cloudant;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.GsonBuilder;

import at.alladin.nettest.couchdb.client.CouchDbClient;
import at.alladin.nettest.couchdb.client.CouchDbDatabase;
import at.alladin.nettest.couchdb.client.config.CouchDbConnectionProperties;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CloudantCouchDbClient implements CouchDbClient {

	private static final Logger logger = LoggerFactory.getLogger(CloudantCouchDbClient.class);
	
	private final CloudantClient cloudantClient;

	private CloudantCouchDbClient(CloudantClient cloudantClient) {
		this.cloudantClient = cloudantClient;
		
		logMetaInformation();
	}
	
	public static CloudantCouchDbClient build(CouchDbConnectionProperties properties) throws MalformedURLException {
		return build(properties, null);
	}
	
	public static CloudantCouchDbClient build(CouchDbConnectionProperties properties, GsonBuilder gsonBuilder) throws MalformedURLException {
		final ClientBuilder clientBuilder = ClientBuilder
				.url(new URL(properties.getUrl()))
				.username(properties.getUsername())
				.password(properties.getPassword())
				.gsonBuilder(gsonBuilder);
		
		properties.getMaxConnections().ifPresent(v -> clientBuilder.maxConnections(v));
		properties.getConnectionTimeout().ifPresent(v -> clientBuilder.connectTimeout(v, TimeUnit.SECONDS));
		properties.getReadTimeout().ifPresent(v -> clientBuilder.readTimeout(v, TimeUnit.SECONDS));
		
		return new CloudantCouchDbClient(clientBuilder.build());
	}
	
	private void logMetaInformation() {
		logger.debug("--- CouchDB meta information ---");
		
		logger.debug("baseUri: {}", cloudantClient.getBaseUri());
		logger.debug("allDbs: {}", cloudantClient.getAllDbs());
		logger.debug("serverVersion: {}", cloudantClient.serverVersion());
		logger.debug("couchDb: {}", cloudantClient.metaInformation().getCouchdb());
		logger.debug("uuid: {}", cloudantClient.metaInformation().getUuid());
		logger.debug("version: {}", cloudantClient.metaInformation().getVersion());
		logger.debug("features: {}", cloudantClient.metaInformation().getFeatures());
		logger.debug("vendor: {}", cloudantClient.metaInformation().getVendor());
		
		logger.debug("------");
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbClient#getDatabase(java.lang.String, boolean)
	 */
	@Override
	public CouchDbDatabase getDatabase(String name, boolean createIfNotExists) {
		final Database cloudantDatabase = cloudantClient.database(name, createIfNotExists);
		return new CloudantCouchDbDatabase(cloudantDatabase);
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbClient#shutdown()
	 */
	@Override
	public void shutdown() {
		cloudantClient.shutdown();
	}
}
