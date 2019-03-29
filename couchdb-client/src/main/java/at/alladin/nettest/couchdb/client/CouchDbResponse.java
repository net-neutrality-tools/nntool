package at.alladin.nettest.couchdb.client;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface CouchDbResponse {

	String getId();

    String getRev();

    String getError();

    String getReason();

    int getStatusCode();
    
    default boolean hasError() {
    	return getError() != null;
    }
}
