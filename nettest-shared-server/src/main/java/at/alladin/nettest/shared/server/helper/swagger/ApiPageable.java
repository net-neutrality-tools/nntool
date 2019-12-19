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

package at.alladin.nettest.shared.server.helper.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

/**
 * Meta-Annotation to correctly display Spring Pageable type in Swagger. 
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
	@ApiImplicitParam(name = "page", dataType = "int", 	  paramType = "query", defaultValue = "0", value = "Number of page to receive (first page is 0)."),
    @ApiImplicitParam(name = "size", dataType = "int",    paramType = "query", defaultValue = "20-50, depending on the data", value = "Number of entities per page."),
    @ApiImplicitParam(name = "sort", dataType = "string", paramType = "query", allowMultiple = true, value = "Sort definition in the form of: property(,asc|desc). Ascending order is default. Multiple occurences of &sort= are possible.")
})
public @interface ApiPageable {

}
