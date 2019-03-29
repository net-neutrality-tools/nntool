package at.alladin.nettest.couchdb.client;

import java.util.List;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public interface CouchDbQueryResult<T> {

	List<T> getDocs();

    String getWarning();

//    public ExecutionStats getExecutionStats() {
//        return executionStats;
//    }
	
}
