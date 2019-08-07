package at.alladin.nettest.spring.data.couchdb.core.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface View {

	@AliasFor("viewName")
	String value() default "";

	@AliasFor("value")
	String viewName() default "";
	
	String designDocument() default "";
	
	boolean reduce() default false;
	
	boolean includeDocs() default false;
	
	boolean descending() default false;
	
	boolean group() default false;
	
	int limit() default 0;
	
	int skip() default 0;
	
	Key[] keys() default {};
	
	Key[] startKey() default {};
	
	Key[] endKey() default {};
}
