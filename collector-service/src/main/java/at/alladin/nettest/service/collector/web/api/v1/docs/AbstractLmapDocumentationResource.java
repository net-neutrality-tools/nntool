package at.alladin.nettest.service.collector.web.api.v1.docs;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import at.alladin.nettest.shared.server.helper.JsonHelper;
import at.alladin.nettest.shared.server.web.api.AbstractDocumentationResource;

/**
 * Abstract documentation resource that adds calls for LMAP control and report model.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
abstract public class AbstractLmapDocumentationResource extends AbstractDocumentationResource {

	/**
	 * Determines a list of DTO classes for the LMAP control model.
	 * 
	 * @return The list of DTO classes.
	 */
	public abstract List<Class<?>> getLmapControlClasses();
	
	/**
	 * Determines a list of DTO classes for the LMAP report model.
	 * 
	 * @return The list of model classes.
	 */
	public abstract List<Class<?>> getLmapReportClasses();
	
	/**
	 * Returns the JSON schema of the REST API DTO LMAP control model.
	 * 
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Returns the JSON schema of the REST API DTO LMAP control model.", notes = "")
	@GetMapping(value = "/json-schemas/lmap-control-model", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Map<String, Object>> renderControlDataModelJsonSchema() {
		return ResponseEntity.ok(JsonHelper.renderJsonSchemaForClasses(getLmapControlClasses()));
	}
	
	/**
	 * Returns the JSON schema of the REST API DTO LMAP report model.
	 * 
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Returns the JSON schema of the REST API DTO LMAP report model.", notes = "")
	@GetMapping(value = "/json-schemas/lmap-report-model", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Map<String, Object>> renderReportDataModelJsonSchema() {
		return ResponseEntity.ok(JsonHelper.renderJsonSchemaForClasses(getLmapReportClasses()));
	}
}
