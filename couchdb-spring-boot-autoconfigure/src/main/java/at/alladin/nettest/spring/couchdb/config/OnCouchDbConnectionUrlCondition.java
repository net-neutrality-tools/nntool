package at.alladin.nettest.spring.couchdb.config;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.OnPropertyListCondition;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
class OnCouchDbConnectionUrlCondition extends OnPropertyListCondition {

	OnCouchDbConnectionUrlCondition() {
		super("spring.couchdb.connection.url", () -> ConditionMessage.forCondition("CouchDB connection URL"));
	}
}
