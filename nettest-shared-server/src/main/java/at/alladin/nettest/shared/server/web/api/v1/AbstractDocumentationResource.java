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

package at.alladin.nettest.shared.server.web.api.v1;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import at.alladin.nettest.shared.server.helper.JsonHelper;

/**
 * This class is used to generate/display documentation of the REST API.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public abstract class AbstractDocumentationResource {

	/**
	 * Determines the OpenAPI group in order to redirect to the correct one.
	 * 
	 * @return The correct OpenAPI group name.
	 */
	public abstract String getOpenApiGroupName();
	
	/**
	 * Determines a list of DTO classes for this REST API.
	 * 
	 * @return The list of DTO classes.
	 */
	public abstract List<Class<?>> getDtoClasses();
	
	/**
	 * Determines a list of model classes.
	 * 
	 * @return The list of model classes.
	 */
	public abstract List<Class<?>> getModelClasses();
	
	////
	
	/**
	 * Redirects to the swagger UI.
	 * This request redirects the user to the swagger UI at /swagger-ui.html.
	 * 
	 * @param response
	 * @throws IOException
	 */
	@io.swagger.annotations.ApiOperation(value = "Redirects to the swagger UI.", notes = "This request redirects the user to the swagger UI at /swagger-ui.html.")
	@ResponseStatus(code = HttpStatus.MOVED_PERMANENTLY)
	@GetMapping({"", "/", "/swagger-ui"})
	public void redirectToSwaggerUi(HttpServletResponse response) throws IOException {
		response.sendRedirect("/swagger-ui.html");
	}
	
	/**
	 * Redirects to the Swagger/OpenApi specification.
	 * This request redirects the user to the Swagger/OpenAPI specification at /v2/api-docs?group=<group>.
	 * 
	 * @param response
	 * @throws IOException
	 */
	@io.swagger.annotations.ApiOperation(value = "Redirects to the OpenApi specification.", notes = "This request redirects the user to the OpenAPI specification at /v2/api-docs?group=<group>.")
	@ResponseStatus(code = HttpStatus.MOVED_PERMANENTLY)
	@GetMapping("/swagger-spec")
	public void redirectToSwaggerSpec(HttpServletResponse response) throws IOException {
		response.sendRedirect("/v2/api-docs?group=" + getOpenApiGroupName());
	}
	
	/**
	 * Returns the JSON schema of the REST API DTO classes.
	 * 
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Returns the json schema of the REST API DTO classes.", notes = "This returns the JSON schema of the REST API DTO classes.")
	@GetMapping(value = "/json-schemas/api", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Map<String, Object>> renderApiJsonSchema() {
		return ResponseEntity.ok(JsonHelper.renderJsonSchemaForClasses(getDtoClasses()));
	}
	
	/**
	 * Returns the json schema of the data model classes.
	 * 
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Returns the json schema of the data model classes.", notes = "This returns the JSON schema of the data model classes.")
	@GetMapping(value = "/json-schemas/data-model", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Map<String, Object>> renderDataModelJsonSchema() {
		return ResponseEntity.ok(JsonHelper.renderJsonSchemaForClasses(getModelClasses()));
	}
}
