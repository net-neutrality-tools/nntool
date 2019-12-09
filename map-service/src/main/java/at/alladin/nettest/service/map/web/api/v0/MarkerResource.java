package at.alladin.nettest.service.map.web.api.v0;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import javax.inject.Inject;

import at.alladin.nettest.service.map.service.MarkerService;
import at.alladin.nntool.shared.map.MapMarkerRequest;
import at.alladin.nntool.shared.map.MapMarkerResponse;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping("/api/v0/tiles/markers")
public class MarkerResource {

    @Inject
	private MarkerService markerService;
    
	//TODO: BIG! Restrict CrossOrigin requests if possible
	@CrossOrigin
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<MapMarkerResponse> obtainMarker(
    		@RequestBody MapMarkerRequest request,
			@ApiIgnore Locale locale) {
    	return ResponseEntity.ok(markerService.obtainMarker(request, locale));
    }
	
}
