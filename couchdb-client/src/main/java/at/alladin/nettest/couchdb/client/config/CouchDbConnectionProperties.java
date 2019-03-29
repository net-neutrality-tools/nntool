package at.alladin.nettest.couchdb.client.config;

import java.util.Optional;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbConnectionProperties {

	private String url = "http://localhost:5984";
	
    private String username;
    private String password;
    
    private Optional<Integer> maxConnections = Optional.empty();
    private Optional<Long> connectionTimeout = Optional.empty();
    private Optional<Long> readTimeout = Optional.empty();

    public String getUrl() {
		return url;
	}
    
    public void setUrl(String url) {
		this.url = url;
	}

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Optional<Integer> getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(Optional<Integer> maxConnections) {
		this.maxConnections = maxConnections;
	}

	public Optional<Long> getConnectionTimeout() {
		return connectionTimeout;
	}
	
	public void setConnectionTimeout(Optional<Long> connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	public Optional<Long> getReadTimeout() {
		return readTimeout;
	}
	
	public void setReadTimeout(Optional<Long> readTimeout) {
		this.readTimeout = readTimeout;
	}
}
