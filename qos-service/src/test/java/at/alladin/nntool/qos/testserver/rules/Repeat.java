package at.alladin.nntool.qos.testserver.rules;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface Repeat {
	
	public int value() default 1;
	
}