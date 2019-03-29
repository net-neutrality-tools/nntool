package at.alladin.nettest.shared.server.storage.couchdb.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CouchDbStorageConfiguration.class)
public @interface EnableCouchDbStorage {

}
