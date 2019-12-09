package at.alladin.nettest.service.map.web.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nntool.shared.map.MarkerResponse;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/markers/default")
public class DefaultMarkerResource extends AbstractMarkerResource {

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.service.map.web.api.v2.AbstractMarkerResource#createMarkers()
	 */
	@Override
	public ResponseEntity<ApiResponse<MarkerResponse>> createMarkers() {
		return ResponseEntity.ok(null);
	}
}
