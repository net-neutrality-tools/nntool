/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
