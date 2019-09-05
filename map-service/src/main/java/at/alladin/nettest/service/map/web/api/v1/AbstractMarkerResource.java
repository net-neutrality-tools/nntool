package at.alladin.nettest.service.map.web.api.v1;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nntool.shared.map.MarkerRequest;
import at.alladin.nntool.shared.map.MarkerResponse;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public abstract class AbstractMarkerResource {
	
	/**
	 * 
	 * @return
	 */
	public abstract ResponseEntity<ApiResponse<MarkerResponse>> createMarkers();
	
    /**
     * 
     * @return
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<MarkerResponse>> getMarkers(ApiRequest<MarkerRequest> markerApiRequest) {
    	return createMarkers();
    }
}
