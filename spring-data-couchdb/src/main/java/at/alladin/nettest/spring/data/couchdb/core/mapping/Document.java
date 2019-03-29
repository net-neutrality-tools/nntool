package at.alladin.nettest.spring.data.couchdb.core.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.annotation.Persistent;

/**
 * Identifies a domain object to be persisted to CouchDB.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Document {

	/**
	 * The entity document type which identifies the class of this document. If not configured, the entity's simple class name will be used.
	 * @return the document type to be used.
	 */
	@AliasFor("docType")
	String value() default "";

	/**
	 * The entity document type which identifies the class of this document. If not configured, the entity's simple class name will be used.
	 * @return the document type to be used.
	 */
	@AliasFor("value")
	String docType() default "";
	
	/**
	 * The database name in which this entity should be stored. If not configured, the entity to database mapping from the configuration
	 * file will be used. If there is no entity/database mapping done, a default database name will be derived from the type's name.
	 * 
	 * @return the name if the database to be used.
	 */
	String database() default "";
}
