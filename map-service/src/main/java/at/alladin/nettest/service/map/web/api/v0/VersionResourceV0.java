package at.alladin.nettest.service.map.web.api.v0;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v0/versions")
public class VersionResourceV0 {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(VersionResourceV0.class);
	
	/**
	 * 
	 * @return
	 */
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVersion(@ApiIgnore Locale locale) {
		final Map<String, Object> versionMap = new HashMap<>();
		
		versionMap.put("version", "1.0.0"); //TODO

		return new ResponseEntity<>(versionMap, HttpStatus.OK);
	}
}
