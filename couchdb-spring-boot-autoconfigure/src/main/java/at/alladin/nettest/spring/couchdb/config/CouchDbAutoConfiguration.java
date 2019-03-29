package at.alladin.nettest.spring.couchdb.config;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
@ConditionalOnClass({ CloudantClient.class, Database.class })
@Conditional(CouchDbAutoConfiguration.CouchDbCondition.class)
@EnableConfigurationProperties(CouchDbProperties.class)
public class CouchDbAutoConfiguration {

    @Configuration
	@ConditionalOnMissingBean(value = CouchDbConfiguration.class, type = "at.alladin.nettest.spring.data.couchdb.config.CouchDbConfigurer")
	@Import(CouchDbConfiguration.class)
	static class DefaultCouchDbConfiguration {

	}
	
    static class CouchDbCondition extends AnyNestedCondition {

    	CouchDbCondition() {
			super(ConfigurationPhase.REGISTER_BEAN);
		}
   
    	@Conditional(OnCouchDbConnectionUrlCondition.class)
		static class ConnectionUrlProperty {

		}
    	
    	@ConditionalOnBean(type = "at.alladin.nettest.spring.data.couchdb.config.CouchDbConfigurer")
		static class CouchDbConfigurerAvailable {

		}
	}
}
