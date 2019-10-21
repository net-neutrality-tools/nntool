package at.alladin.nettest.service.map.web.api.v1;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nntool.shared.map.TileInfoResponse;
import at.alladin.nntool.shared.map.TileRequest;
import io.swagger.annotations.ApiParam;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public abstract class AbstractTileResource {

	/**
	 * 
	 * @param zoom
	 * @param x
	 * @param y
	 * @param tileApiRequest
	 * @return
	 */
	public abstract ResponseEntity<byte[]> createTileByZoomXY(Integer zoom, Integer x, Integer y, ApiRequest<TileRequest> tileApiRequest);
	
	/**
	 * 
	 * @return
	 */
	public abstract ResponseEntity<ApiResponse<TileInfoResponse>> getInfos();
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param zoom
	 * @param tileApiRequest
	 * @return
	 */
	@GetMapping(value = "/{zoom}/{x}/{y}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getTileByZoomXY(
    	@PathVariable Integer zoom, @PathVariable Integer x, @PathVariable Integer y, 
    	@ApiParam(required = true) ApiRequest<TileRequest> tileApiRequest) {
    	
    	return createTileByZoomXY(zoom, x, y, tileApiRequest);
    }

    //@PostMapping(value="/", produces=MediaType.IMAGE_PNG_VALUE)
    //public abstract ResponseEntity<byte[]> getTile(@RequestBody MapTileRequest request);
	
    /**
     * 
     * @return
     */
    @GetMapping(value = "/infos", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<TileInfoResponse>> getTileInfos() {
    	return getInfos();
    }
}
